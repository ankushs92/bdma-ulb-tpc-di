package bdma.ulb.tpcdi

import bdma.ulb.tpcdi.domain.*
import bdma.ulb.tpcdi.domain.enums.BatchId
import bdma.ulb.tpcdi.domain.enums.Gender
import bdma.ulb.tpcdi.domain.enums.Status
import bdma.ulb.tpcdi.domain.keys.CustomerXmlKeys
import bdma.ulb.tpcdi.domain.keys.ProspectRecordKeys
import bdma.ulb.tpcdi.repository.*
import bdma.ulb.tpcdi.util.DateTimeUtil
import bdma.ulb.tpcdi.util.FileParser
import bdma.ulb.tpcdi.util.Strings
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate
import java.time.Month

import static bdma.ulb.tpcdi.domain.Constants.*
import static bdma.ulb.tpcdi.domain.Constants.COMMA_DELIM
import static bdma.ulb.tpcdi.domain.Constants.CUSTOMER_XML
import static bdma.ulb.tpcdi.util.Strings.NULL
import static bdma.ulb.tpcdi.util.Strings.hasText
import static bdma.ulb.tpcdi.util.Strings.toLowerCaseIfAnyMixedCase

@Component
@Slf4j
class HistoricalLoad {


    private final String directoryLocation

    private final DimAccountRepository dimAccountRepository
    private final DimDateRepository dimDateRepository
    private final DimTimeRepository dimTimeRepository
    private final IndustryRepository industryRepository
    private final StatusTypeRepository statusTypeRepository
    private final TaxRateRepository taxRateRepository
    private final TradeTypeRepository tradeTypeRepository
    private final DimBrokerRepository dimBrokerRepository
    private final DimCompanyRepository dimCompanyRepository
    private final DimSecurityRepository dimSecurityRepository
    private final FinancialRepository financialRepository
    private final DimCustomerRepository dimCustomerRepository

    HistoricalLoad(
            @Value("\${file.location}") String directoryLocation,
            DimAccountRepository dimAccountRepository,
            DimDateRepository dimDateRepository,
            DimTimeRepository dimTimeRepository,
            IndustryRepository industryRepository,
            StatusTypeRepository statusTypeRepository,
            TaxRateRepository taxRateRepository,
            TradeTypeRepository tradeTypeRepository,
            DimBrokerRepository dimBrokerRepository,
            DimCompanyRepository dimCompanyRepository,
            DimSecurityRepository dimSecurityRepository,
            FinancialRepository financialRepository,
            DimCustomerRepository dimCustomerRepository
    )
    {
        this.directoryLocation = directoryLocation
        this.dimAccountRepository = dimAccountRepository
        this.dimDateRepository = dimDateRepository
        this.dimTimeRepository = dimTimeRepository
        this.industryRepository = industryRepository
        this.statusTypeRepository = statusTypeRepository
        this.taxRateRepository = taxRateRepository
        this.tradeTypeRepository = tradeTypeRepository
        this.dimBrokerRepository = dimBrokerRepository
        this.dimCompanyRepository = dimCompanyRepository
        this.dimSecurityRepository = dimSecurityRepository
        this.financialRepository = financialRepository
        this.dimCustomerRepository = dimCustomerRepository
    }

    @PostConstruct
    void parseFilesAndPerformHistoralLoad() {
        log.info "Preparing to parse all csv and txt files"
        List<File> files = []
        def directory = new File(directoryLocation)
        directory.eachFile { file ->
            files << file
        }

        def dimDateFile = getFile(files, DATE_TXT)
        log.info "Loading data into DimDate"
        List<DimDate> dimDates = parseDimDate(dimDateFile)
        dimDateRepository.saveAll(dimDates)

        def dimTimeFile = getFile(files, TIME_TXT)
        log.info "Loading data into DimTime"
        List<DimTime> dimTimes = parseDimTime(dimTimeFile)
        dimTimeRepository.saveAll(dimTimes)

        def industryFile = getFile(files, INDUSTRY_TXT)
        log.info "Loading data into Industry"
        List<Industry> industries = parseIndustry(industryFile)
        industryRepository.saveAll(industries)

        def statusTypeFile = getFile(files, STATUS_TYPE_TXT)
        log.info "Loading data into StatusType"
        List<StatusType> statusTypes = parseStatusType(statusTypeFile)
        statusTypeRepository.saveAll(statusTypes)

        def taxRateFile = getFile(files, TAX_RATE_TXT)
        log.info "Loading data into TaxRate"
        List<TaxRate> taxRates = parseTaxRates(taxRateFile)
        taxRateRepository.saveAll(taxRates)

        def tradeTypeFile = getFile(files, TRADE_TYPE_TXT)
        log.info "Loading data into TradeType"
        List<TradeType> tradeTypes = parseTradeTypes(tradeTypeFile)
        tradeTypeRepository.saveAll(tradeTypes)

        def dimBrokerFile = getFile(files, DIM_BROKER_CSV)
        def earliestDate = dimDates.sort { it.date }.find().date
        log.info "Loading data into DimBroker"
        List<DimBroker> dimBrokers = parseDimBrokers(dimBrokerFile, earliestDate)
        dimBrokerRepository.saveAll(dimBrokers)

        log.info "Parsing all FinWire txt files"
        def finWireFiles = getTxtFilesThatStartWithName(files, FINWIRE_RECORDS_CSV_INITIAL_NAME)
        //Since we will be needing them further down the line, we parse all of them in one go and keep them in memory
        def finWireRecords = finWireFiles.collect { file -> file.readLines() }.flatten() as List<String>
        List<DimCompany> dimCompanies = parseDimCompanies(finWireRecords, industries, statusTypes, earliestDate)
        log.info "Loading data into DimCompany"
        dimCompanyRepository.saveAll(dimCompanies)

        List<DimSecurity> dimSecurities = parseDimSecurity(finWireRecords, dimCompanies, statusTypes)
        log.info "Loading data into DimSecurity"
        dimSecurityRepository.saveAll(dimSecurities)

        List<Financial> financials = parseFinancials(finWireRecords, dimCompanies)
        log.info "Loading data into Financial"
        financialRepository.saveAll(financials)

        //We need prospect file records before DimCustomer, but the data has to be saved after data has been saved for DimCustomer
        def prospectFile = getFile(files, PROSPECT_CSV)
        List<String[]> prospectFileRecords = getProspectFileRecords(prospectFile)

        def customerXmlFile = getFile(files, CUSTOMER_XML)
        List<DimCustomer> dimCustomers = parseDimCustomer(customerXmlFile, taxRates, prospectFileRecords)
        log.info "Loading data into DimCustomer"
        dimCustomers = dimCustomerRepository.saveAll(dimCustomers)


    }


    private static File getFile(List<File> files, String fileName) {
        files.find { it.name == fileName }
    }

    private static List<File> getTxtFilesThatStartWithName(List<File> files, String fileName) {
        files.findAll { it.name.startsWith(fileName) && !it.name.endsWith("csv") } //No format specified
    }


    private static List<DimDate> parseDimDate(File file) {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        records.collect { record ->
            new DimDate(
                id : record[0] as Integer ,
                date : DateTimeUtil.parse(record[1]),
                desc : record[2],
                calenderYearId : record[3] as Integer,
                calenderYearDesc : record[4],
                calenderQtrlId : record[5] as Integer,
                calenderQtrlDesc : record[6],
                calenderMonthId : record[7] as Integer,
                calenderMonthDesc : record[8],
                dayOfWeek : record[9] as Integer,
                dayOfWeekDesc : record[10],
                fiscalYearNum : record[11] as Integer,
                fiscalYearDesc : record[12],
                fiscalQtrlId : record[13] as Integer,
                fiscalQtrlDesc : record[14],
                isHoliday : record[15]?.toBoolean()
            )

        }
    }

    private static List<DimTime> parseDimTime(File file) {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        records.collect { record ->
            new DimTime(
                    id : record[0] as Integer ,
                    time : DateTimeUtil.asLocalTime(record[1]),
                    hourId : record[2] as Integer,
                    hourDesc : record[3],
                    minuteId : record[4] as Integer,
                    minuteDesc : record[5],
                    secondId : record[6] as Integer,
                    secondDesc : record[7],
                    isMarketHours : record[8]?.toBoolean(),
                    isOfficeHours : record[9]?.toBoolean()
            )
        }
    }

    private static List<Industry> parseIndustry(File file) {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        records.collect { record ->
            new Industry(
                    id : record[0] ,
                    name : record[1],
                    scId : record[2]
            )
        }
    }

    private static List<StatusType> parseStatusType(File file) {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        records.collect { record ->
            new StatusType(
                    id : record[0] ,
                    name : record[1]
            )
        }
    }


    private static List<TaxRate> parseTaxRates(File file) {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        records.collect { record ->
            new TaxRate(
                    id : record[0] ,
                    name : record[1],
                    rate : record[2] as Double
            )
        }
    }

    private static List<TradeType> parseTradeTypes(File file) {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        records.collect { record ->
            new TradeType(
                    id : record[0] ,
                    name : record[1],
                    isSell : record[2]?.toBoolean(),
                    isMarketOrder : record[2]?.toBoolean(),
            )
        }
    }

    private static List<DimBroker> parseDimBrokers(File file, LocalDate earliestDate) {
        List<String[]> records = FileParser.parse(file.path, COMMA_DELIM)
        records.findAll { record ->
                    def jobCode = record[5]
                    boolean filter
                    if(hasText(jobCode)) {
                        jobCode = jobCode as Integer
                        filter = jobCode == BROKER_JOB_CODE // Check section in spec : 4.5.2.1
                    }
                    filter
                }
                .collect { record ->
                    new DimBroker(
                            brokerId : record[0] as Integer,
                            managerId : record[1] as Integer,
                            firstName : record[2],
                            lastName : record[3],
                            middleInitial : record[4],
                            branch : record[6],
                            office : record[7],
                            phone : record[8],
                            isCurrent : true,
                            effectiveDate : earliestDate,
                            endDate : LocalDate.of(9999, Month.DECEMBER, 31),
                            batchId : BatchId.HISTORICAL_LOAD
                    )
                }
    }

    private static List<DimCompany> parseDimCompanies(
            List<String> records,
            List<Industry> industries,
            List<StatusType> statusTypes,
            LocalDate earliestDate
    )
    {
        records.findAll { record ->
                    //Refer to section 2.2.2.8
                    def recType = record.substring(15, 18)
                    recType == "CMP"
                }
                .collect { record ->
                    def companyName = hasText(record.substring(18, 78)) ? record.substring(18, 78) : NULL
                    def cik = record.substring(78, 88) as Integer
                    def status = Status.from(statusTypes.find { it.id == record.substring(88, 92)}.name)
                    def industryId = record.substring(92, 94)
                    def spRating = hasText(record.substring(94, 98)) ? record.substring(94, 98) : NULL
                    def foundingDate = hasText(record.substring(98, 106)) ? DateTimeUtil.parseFinWireDate(record.substring(98, 106)) : null
                    def addrLine1 = hasText(record.substring(106, 186)) ? record.substring(106, 186) : NULL
                    def addrLine2 = hasText(record.substring(186, 266)) ? record.substring(186, 266) : NULL
                    def postalCode = hasText(record.substring(266, 278)) ? record.substring(266, 278) : NULL
                    def city = hasText(record.substring(278, 303)) ? record.substring(278, 303) : NULL
                    def stateProv = hasText(record.substring(303, 323)) ? record.substring(303, 323) : NULL
                    def country = hasText(record.substring(323, 347)) ? record.substring(323, 347) : NULL
                    def ceo = hasText(record.substring(347, 393)) ? record.substring(347, 393) : NULL
                    def desc = hasText(record.substring(393, record.size())) ? record.substring(393, record.size()) : NULL
                    def industry = industries.find { it.id == industryId }.name

                    boolean isLowGrade = !(spRating.startsWith("A") || spRating.startsWith("BBB"))

                    new DimCompany(
                        companyId : cik,
                        status : status,
                        name : companyName,
                        industry : industry,
                        spRating : spRating,
                        isLowGrade : isLowGrade,
                        ceo : ceo,
                        addressLine1 : addrLine1,
                        addressLine2 :  addrLine2,
                        postalCode : postalCode,
                        city : city,
                        country : country,
                        stateProv : stateProv,
                        desc : desc,
                        foundingDate : foundingDate,
                        isCurrent : true,
                        effectiveDate : earliestDate,
                        endDate : LocalDate.of(9999, Month.DECEMBER, 31),
                        batchId : BatchId.HISTORICAL_LOAD
                    )
        }
    }


    private static List<DimSecurity> parseDimSecurity(
            List<String> records,
            List<DimCompany> dimCompanies,
            List<StatusType> statusTypes
    )
    {
        records.findAll { record ->
                    def recType = record.substring(15,18)
                    return recType == "SEC"
                }
                .collect{ record ->
                    def pts = DateTimeUtil.parseFinWireDateAndTime(record.substring(0, 15)).toLocalDate()
                    def symbol = record.substring(18, 33)
                    def issueType = record.substring(33, 39)
                    def status = Status.from(statusTypes.find { it.id == record.substring(39, 43)}.name)
                    def name = record.substring(43, 113)
                    def exchangeId = record.substring(113, 119)
                    def sharesOutstanding = record.substring(119, 132) as Integer
                    def firstDate = DateTimeUtil.parseFinWireDate(record.substring(132, 140))
                    def firstTradeOnExchange = DateTimeUtil.parseFinWireDate(record.substring(140, 148))
                    def dividend = record.substring(148, 160) as Double
                    def coNameOrCik = record.substring(160, record.size())
                    def isCurrent = true
                    def security = "security" //TODO CHANGE LATER
                    def effectiveDate = pts
                    def endDate = LocalDate.of(9999, Month.DECEMBER, 31)
                    def batchId = BatchId.HISTORICAL_LOAD
                    def skCompanyId
                    if(
                     (pts == effectiveDate || pts.isAfter(effectiveDate))
                        && (pts.isBefore(endDate))
                    )
                    {
                        if(coNameOrCik.size() == 10) {
                            skCompanyId = dimCompanies.find { company ->
                                company.companyId == (coNameOrCik as Integer)
                            }.id
                        }
                        else {
                            skCompanyId = dimCompanies.find { company ->
                                company.name == coNameOrCik
                            }.id
                        }
                    }
                    new DimSecurity(
                        symbol : symbol,
                        issue : issueType,
                        status : status,
                        name : name,
                        exchangeId : exchangeId,
                        security : security,
                        skCompanyId : skCompanyId,
                        sharesOutstanding : sharesOutstanding,
                        firstDate : firstDate,
                        firstTradeOnExchange : firstTradeOnExchange,
                        dividend : dividend,
                        isCurrent : isCurrent,
                        effectiveDate : effectiveDate,
                        endDate : endDate,
                        batchId : batchId
                    )
             }
    }

    private static List<Financial> parseFinancials(
            List<String> records,
            List<DimCompany> dimCompanies
    )
    {
        records.findAll { record ->
                    def recType = record.substring(15,18)
                    return recType == "FIN"
               }
               .collect { record ->
                    def pts = DateTimeUtil.parseFinWireDateAndTime(record.substring(0, 15)).toLocalDate()
                    def year = record.substring(18, 22) as Integer
                    def qtr = record.substring(22, 23) as Integer
                    def qtrStartDate = DateTimeUtil.parseFinWireDate(record.substring(23, 31))
                    def revenue = record.substring(39, 56) as Double
                    def fiNetEarn = record.substring(56, 73) as Double
                    def eps = record.substring(73, 85) as Double
                    def dilutedEps = record.substring(85, 97) as Double
                    def margin = record.substring(97, 109) as Double
                    def inventory = record.substring(109, 126) as Double
                    def assets = record.substring(126, 143) as Double
                    def liability = record.substring(143, 160) as Double
                    def basicOut = record.substring(160, 173) as Integer
                    def dilutedShOut = record.substring(173, 186) as Integer
                    def coNameOrCik = record.substring(186, record.size())
                    def effectiveDate = pts
                    def endDate = LocalDate.of(9999, Month.DECEMBER, 31)

                    def skCompanyId
                    if(
                    (pts == effectiveDate || pts.isAfter(effectiveDate))
                            && (pts.isBefore(endDate))
                    )
                    {
                        if(coNameOrCik.size() == 10) {
                            skCompanyId = dimCompanies.find { company ->
                                company.companyId == (coNameOrCik as Integer)
                            }.id
                        }
                        else {
                            skCompanyId = dimCompanies.find { company ->
                                company.name == coNameOrCik
                            }.id
                        }
                    }

                    new Financial(
                            id: skCompanyId,
                            fiYear : year,
                            fiQtr : qtr,
                            startDate : qtrStartDate,
                            revenue : revenue,
                            fiNetEarn : fiNetEarn,
                            fiBasicEps : eps,
                            fiDilutEps : dilutedEps,
                            fiMargin : margin,
                            fiInventory : inventory,
                            fiAssets : assets,
                            fiLiability : liability ,
                            fiOutBasic : basicOut,
                            fiOutDilut : dilutedShOut
                    )


        }
    }


    private static List<DimCustomer> parseDimCustomer(
            File file,
            List<TaxRate> taxRates,
            List<String[]> prospectRecords
    )
    {
        //Parse customer management xml
        def xmlRootNode = new XmlSlurper().parseText(file.text)
        def dimCompanies = []
        Map<ProspectRecordKeys, String[]> prospectCache = prospectRecords.collectEntries { String[] prospectRecord ->
            String prospectLastName = prospectRecord[1]
            String prospectFirstName = prospectRecord[2]
            String prospectAddressline1 = prospectRecord[5]
            String prospectAddressline2 = prospectRecord[6]
            String prospectPostalcode = prospectRecord[7]
            [new ProspectRecordKeys(
                    firstName : toLowerCaseIfAnyMixedCase(prospectFirstName),
                    lastName : toLowerCaseIfAnyMixedCase(prospectLastName),
                    addressline1 : toLowerCaseIfAnyMixedCase(prospectAddressline1),
                    addressline2 : toLowerCaseIfAnyMixedCase(prospectAddressline2),
                    postalCode : toLowerCaseIfAnyMixedCase(prospectPostalcode)
            ), prospectRecord]
        }

        int count = 0

        Map<CustomerXmlKeys, Object> xmlUpdateOrInactCache = [:]
        xmlRootNode.Action.each { action ->
            def actionType = action.@ActionType as String
            def customer = action.Customer
            def customerId = customer.@C_ID?.toInteger()
            if(actionType == "UPDCUST" || actionType == "INACT") {
                xmlUpdateOrInactCache.put(new CustomerXmlKeys(actionType : actionType, customerId : customerId), action)
            }
        }

        xmlRootNode.Action.each { action ->
            def actionTimestamp = DateTimeUtil.parseIso(action.@ActionTS as String)
            def actionType = action.@ActionType as String
            def customer = action.Customer
            def customerId = customer.@C_ID?.toInteger()
            def taxId = customer.@C_TAX_ID as String
            def gender = Gender.from(customer.@C_GNDR as String)
            def tier = customer.@C_TIER?.toInteger()
            def dobStr = customer.@C_DOB as String
            def dob = hasText(dobStr) ? DateTimeUtil.parse(dobStr) : null

            def name = customer.Name
            def firstName = name.C_F_NAME as String
            def lastName = name.C_L_NAME as String
            def middleName = name.C_M_NAME as String

            def address = customer.Address
            def addressLine1 = address.C_ADLINE1 as String
            def addressLine2 = address.C_ADLINE2 as String
            def postalCode = address.C_ZIPCODE as String
            def city = address.C_CITY as String
            def stateProv = address.C_STATE_PROV as String
            def country = address.C_CTRY as String

            def contactInfo = customer.ContactInfo
            def email1 = contactInfo.C_PRIM_EMAIL as String
            def email2 = contactInfo.C_ALT_EMAIL as String
            def phone1Local = customer.C_PHONE_1.C_LOCAL as String
            def phone1CtryCode = customer.C_PHONE_1.C_CTRY_CODE as String
            def phone1AreaCode = customer.C_PHONE_1.C_AREA_CODE as String
            def phone1Ext = customer.C_PHONE_1.C_EXT as String
            def phone1 = buildPhoneNum(phone1CtryCode, phone1AreaCode, phone1Local, phone1Ext)

            def phone2Local = customer.C_PHONE_2.C_LOCAL as String
            def phone2CtryCode = customer.C_PHONE_2.C_CTRY_CODE as String
            def phone2AreaCode = customer.C_PHONE_2.C_AREA_CODE as String
            def phone2Ext = customer.C_PHONE_2.C_EXT as String
            def phone2 = buildPhoneNum(phone2CtryCode, phone2AreaCode, phone2Local, phone2Ext)

            def phone3Local = customer.C_PHONE_3.C_LOCAL as String
            def phone3CtryCode = customer.C_PHONE_3.C_CTRY_CODE as String
            def phone3AreaCode = customer.C_PHONE_3.C_AREA_CODE as String
            def phone3Ext = customer.C_PHONE_3.C_EXT as String
            def phone3 = buildPhoneNum(phone3CtryCode, phone3AreaCode, phone3Local, phone3Ext)

            def taxInfo = customer.TaxInfo
            def localTaxId = taxInfo.C_LCL_TX_ID as String
            def nationalTaxId = taxInfo.C_NAT_TX_ID as String

            def localTaxRate = taxRates.find { it.id == localTaxId}
            def nationalTaxRate = taxRates.find { it.id == nationalTaxId}

            def localTaxRateValue = localTaxRate?.rate
            def nationalTaxRateValue = nationalTaxRate?.rate
            def localTaxRateDesc = localTaxRate?.name
            def nationalTaxRateDesc = nationalTaxRate?.name

            def isCurrent = true
            def batchId = BatchId.HISTORICAL_LOAD
            def effectiveDate = actionTimestamp.toLocalDate()
            def endDate = LocalDate.of(9999, Month.DECEMBER, 31)

            //Following fields are in prospect csv file AND NOT IN DIM CUSTOMER CSV, BUT THESE FUCKERS HAVE TO BE ADDED TO DIM CUSTOMER TABLE
            def agencyId, creditRating, marketingNameplate, netWorth

            def updateActionKey = new CustomerXmlKeys(actionType : "UPDCUST", customerId : customerId )
            def inactActionKey = new CustomerXmlKeys(actionType : "INACT", customerId : customerId )

            def customerUpdateAction = xmlUpdateOrInactCache.get(updateActionKey)
            def customerInactAction = xmlUpdateOrInactCache.get(inactActionKey)

            boolean customerHasUpdateAction, customerHasInactAction

            if(customerUpdateAction) customerHasUpdateAction = true
            if(customerInactAction) customerHasInactAction = true
//            boolean isUpdateOrInactive = xmlRootNode.Action.any { updOrInactAction ->
//                if(
//                (updOrInactAction.@ActionType == "UPDCUST" || updOrInactAction.@ActionType == "INACT" )
//               && (updOrInactAction.customer.@C_ID?.toInteger() == customerId)
//                )
//                {
//                    return true
//                }
//            }

            def prospect = prospectCache.get(new ProspectRecordKeys(
                    firstName : toLowerCaseIfAnyMixedCase(firstName),
                    lastName : toLowerCaseIfAnyMixedCase(lastName),
                    addressline1 : toLowerCaseIfAnyMixedCase(addressLine1),
                    addressline2 : toLowerCaseIfAnyMixedCase(addressLine2),
                    postalCode : toLowerCaseIfAnyMixedCase(postalCode)
                )
            )

             if(!customerHasInactAction && !customerHasUpdateAction && prospect) {
                agencyId = prospect[0]
                creditRating = hasText(prospect[17]) ? prospect[17]  as Integer : null
                netWorth = hasText(prospect[21]) ?  prospect[21] as Double : null
                marketingNameplate = buildMarketingNameplate(prospect)
            }

//            if(!isUpdateOrInactive && prospect) {
//                agencyId = prospect[0]
//                creditRating = hasText(prospect[17]) ? prospect[17]  as Integer : null
//                netWorth = hasText(prospect[21]) ?  prospect[21] as Double : null
//                marketingNameplate = buildMarketingNameplate(prospect)
//            }
            def status
            if(actionType == "NEW" || actionType == "UPDCUST" || actionType == "ADDACCT" || actionType == "UPDACCT") {
                status = Status.ACTIVE
            }
            else if (actionType == "INACT" || actionType == "CLOSEACCT") {
                status = Status.INACTIVE
            }

            dimCompanies << new DimCustomer(
                    customerId : customerId,
                    taxId : taxId,
                    status : status,
                    firstName : firstName,
                    middleInitial : middleName,
                    gender : gender,
                    tier : tier,
                    dob : dob,
                    addressLine1 : addressLine1,
                    addressLine2 : addressLine2,
                    postalCode : postalCode,
                    city : city,
                    stateProv : stateProv,
                    country : country,
                    phone1 : phone1,
                    phone2 : phone2,
                    phone3 : phone3,
                    email1 : email1,
                    email2 : email2,
                    nationalTaxRateDesc : nationalTaxRateDesc,
                    nationalTaxRate : nationalTaxRateValue,
                    localTaxRateDesc : localTaxRateDesc,
                    localTaxRate : localTaxRateValue,
                    agencyId : agencyId,
                    creditRating : creditRating,
                    netWorth : netWorth,
                    marketingNameplate : marketingNameplate,
                    batchId : batchId,
                    isCurrent : isCurrent,
                    effectiveDate : effectiveDate,
                    endDate : endDate
            )
        }

        dimCompanies
    }

//    public static void main(String[] args) {
//        def file = new File("/Users/ankushsharma/Downloads/tpc-di-tools/pdgf/output/Batch1/CustomerMgmt.xml")
//
//        def xml = new XmlSlurper()
//        def node = xml.parseText(file.text)
//
//        def actions = node.name()
//
//        for(action in node.Action) {
//            def customer = action.Customer
//
//            def dob = customer.@C_ID
//            println dob
//
//        }
//
//    }

    private static List<String[]> getProspectFileRecords(File file) {
        FileParser.parse(file.path, COMMA_DELIM)
    }

    private static String buildPhoneNum(String ctryCode, String areaCode, String localNum, String ext) {
        //If all are not null and non empty
        def result
        boolean rulesApplied = true
        if(hasText(ctryCode) && hasText(areaCode) && hasText(localNum)) {
            result = "+$ctryCode($areaCode)$localNum"
        }
        else if(!hasText(ctryCode) && hasText(areaCode) && hasText(localNum)) {
            result = "($areaCode)$localNum"
        }
        else if(!hasText(ctryCode) && !hasText(areaCode) && hasText(localNum)) {
            result = "$localNum"
        }
        else {
            rulesApplied = false
        }

        if(!rulesApplied) {
            result = null
        }
        if(rulesApplied && hasText(ext)) {
            result = "$result$ext"
        }
        result
    }


    private static String buildMarketingNameplate(String[] prospect) {
        def tags = []
        def incomeStr = prospect[12]
        def numOfCarsStr = prospect[13]
        def netWorthStr = prospect[21]
        def numOfChildrenStr = prospect[14]
        def numCreditCardsStr = prospect[20]
        def ageStr = prospect[16]
        def creditRatingStr = prospect[17]


        if((hasText(netWorthStr) && (netWorthStr as Integer) > 1000000)|| (hasText(incomeStr) && (incomeStr as Integer) > 200000)) {
            tags << "HighValue"
        }

        if((hasText(numOfChildrenStr) && (numOfChildrenStr as Integer) > 3) || (hasText(numCreditCardsStr) && (numCreditCardsStr as Integer) > 5)) {
            tags << "Expenses"
        }

        if(hasText(ageStr) && (ageStr as Integer) > 45) {
            tags << "Boomer"
        }

        if((hasText(incomeStr) && ( incomeStr as Integer) < 50000)
                || (hasText(creditRatingStr) && (creditRatingStr as Integer) < 600)
                || (hasText(netWorthStr) && (netWorthStr as Integer) < 100000))
        {
            tags << "Spender"
        }

        if((hasText(numOfCarsStr) && (numOfCarsStr as Integer) > 3) || (hasText(numCreditCardsStr) && (numCreditCardsStr as Integer) > 7)) {
            tags << "Spender"
        }

        if(hasText(ageStr) && (ageStr as Integer) < 25 && hasText(netWorthStr) && (netWorthStr as Integer) > 1000000) {
            tags << "Inherited"
        }
        tags.join("+")
    }


}
