package bdma.ulb.tpcdi

import bdma.ulb.tpcdi.domain.*
import bdma.ulb.tpcdi.domain.enums.BatchId
import bdma.ulb.tpcdi.domain.enums.Gender
import bdma.ulb.tpcdi.domain.enums.Status
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
import static bdma.ulb.tpcdi.util.Strings.NULL
import static bdma.ulb.tpcdi.util.Strings.hasText

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
        log.info "Finished parsing $DATE_TXT"
        log.info "Loading data into DimDate"
        List<DimDate> dimDates = parseDimDate(dimDateFile)
        dimDateRepository.saveAll(dimDates)

        def dimTimeFile = getFile(files, TIME_TXT)
        log.info "Finished parsing $TIME_TXT"
        log.info "Loading data into DimTime"
        List<DimTime> dimTimes = parseDimTime(dimTimeFile)
        dimTimeRepository.saveAll(dimTimes)

        def industryFile = getFile(files, INDUSTRY_TXT)
        log.info "Finished parsing $INDUSTRY_TXT"
        log.info "Loading data into Industry"
        List<Industry> industries = parseIndustry(industryFile)
        industryRepository.saveAll(industries)

        def statusTypeFile = getFile(files, STATUS_TYPE_TXT)
        log.info "Finished parsing $STATUS_TYPE_TXT"
        log.info "Loading data into StatusType"
        List<StatusType> statusTypes = parseStatusType(statusTypeFile)
        statusTypeRepository.saveAll(statusTypes)

        def taxRateFile = getFile(files, TAX_RATE_TXT)
        log.info "Finished parsing $TAX_RATE_TXT"
        log.info "Loading data into TaxRate"
        List<TaxRate> taxRates = parseTaxRates(taxRateFile)
        taxRateRepository.saveAll(taxRates)

        def tradeTypeFile = getFile(files, TRADE_TYPE_TXT)
        log.info "Finished parsing $TRADE_TYPE_TXT"
        log.info "Loading data into TradeType"
        List<TradeType> tradeTypes = parseTradeTypes(tradeTypeFile)
        tradeTypeRepository.saveAll(tradeTypes)

        def dimBrokerFile = getFile(files, DIM_BROKER_CSV)
        def earliestDate = dimDates.sort { it.date }.find().date
        log.info "Finished parsing $DIM_BROKER_CSV"
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
        xmlRootNode.Action.each { action ->
            def actionTimestamp = DateTimeUtil.parseIso(action.@ActionTS)
            def actionType = action.@ActionType
            def customer = action.Customer
            def customerId = customer.@C_ID
            def taxId = customer.@C_TAX_ID
            def gender = Gender.from(customer.@C_GNDR)
            def tier = customer.@C_TIER
            def dob = DateTimeUtil.parse(customer.@C_DOB)

            def name = customer.Name
            def firstName = name.C_F_NAME
            def lastName = name.C_L_NAME
            def middleName = name.C_M_NAME

            def address = customer.Address
            def addressLine1 = address.C_ADLINE1
            def addressLine2 = address.C_ADLINE2
            def postalCode = address.C_ZIPCODE
            def city = address.C_CITY
            def stateProv = address.C_STATE_PROV
            def country = address.C_CTRY

            def contactInfo = customer.ContactInfo
            def email1 = contactInfo.C_PRIM_EMAIL
            def email2 = contactInfo.C_ALT_EMAIL
            def phone1Local = customer.C_PHONE_1.C_LOCAL
            def phone1CtryCode = customer.C_PHONE_1.C_CTRY_CODE
            def phone1AreaCode = customer.C_PHONE_1.C_AREA_CODE
            def phone1Ext = customer.C_PHONE_1.C_EXT
            def phone1 = buildPhoneNum(phone1CtryCode, phone1AreaCode, phone1AreaCode, phone1Ext)

            def phone2CtryCode = customer.C_PHONE_2.C_CTRY_CODE
            def phone2AreaCode = customer.C_PHONE_2.C_AREA_CODE
            def phone2Ext = customer.C_PHONE_2.C_EXT
            def phone2 = buildPhoneNum(phone2CtryCode, phone2AreaCode, phone2AreaCode, phone2Ext)

            def phone3CtryCode = customer.C_PHONE_3.C_CTRY_CODE
            def phone3AreaCode = customer.C_PHONE_3.C_AREA_CODE
            def phone3Ext = customer.C_PHONE_3.C_EXT
            def phone3 = buildPhoneNum(phone3CtryCode, phone3AreaCode, phone3AreaCode, phone3Ext)

            def taxInfo = customer.TaxInfo
            def localTaxId = taxInfo.C_LCL_TX_ID
            def nationalTaxId = taxInfo.C_NAT_TX_ID

            def localTaxRate = taxRates.find { it.id == localTaxId}
            def nationalTaxRate = taxRates.find { it.id == nationalTaxId}

            def localTaxRateValue = localTaxRate.rate
            def nationalTaxRateValue = nationalTaxRate.rate
            def localTaxRateDesc = localTaxRate.name
            def nationalTaxRateDesc = nationalTaxRate.name

            def isCurrent = true
            def batchId = BatchId.HISTORICAL_LOAD
            def effectiveDate = actionTimestamp.toLocalDate()
            def endDate = LocalDate.of(9999, Month.DECEMBER, 31)

            def agencyId

            def status
            if(actionType == "NEW") {
                status = Status.ACTIVE
            }

            dimCompanies << new DimCustomer(
                    customerId : customerId,
                    taxId : taxId,
                    status : status,
                    firstName : firstName,
                    middleInitial : middleName,
                    gender : gender,
            )

//            @Column(name = "CustomerID", nullable = false, columnDefinition = "INT(11) UNSIGNED")
//            Integer customerId
//
//            @Column(name = "TaxID", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
//            String taxId
//
//            @Column(name = "Status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT ''")
//            Status status
//
//            @Column(name = "FirstName", nullable = false, columnDefinition = "VARCHAR(30) DEFAULT ''")
//            String firstName
//
//            @Column(name = "MiddleInitial", nullable = false, columnDefinition = "VARCHAR(1) DEFAULT ''")
//            String middleInitial
//
//            @Column(name = "Gender", nullable = false, columnDefinition = "VARCHAR(1) DEFAULT ''")
//            Gender gender
//
//            @Column(name = "Tier", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
//            Integer tier
//
//            @Column(name = "DOB", nullable = false, columnDefinition = "DATE")
//            LocalDate dob
//
//            @Column(name = "AddressLine1", nullable = false, columnDefinition = "VARCHAR(80) DEFAULT ''")
//            String addressLine1
//
//
//            @Column(name = "AddressLine2", columnDefinition = "VARCHAR(80) DEFAULT ''")
//            String addressLine2
//
//            @Column(name = "PostalCode", nullable = false, columnDefinition = "VARCHAR(12) DEFAULT ''")
//            String postalCode
//
//            @Column(name = "City", nullable = false, columnDefinition = "VARCHAR(25) DEFAULT ''")
//            String city
//
//            @Column(name = "StateProv", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
//            String stateProv
//
//            @Column(name = "Phone1", nullable = false, columnDefinition = "VARCHAR(30) DEFAULT ''")
//            String phone1
//
//            @Column(name = "Phone2", nullable = false, columnDefinition = "VARCHAR(30) DEFAULT ''")
//            String phone2
//
//            @Column(name = "Phone3", nullable = false, columnDefinition = "VARCHAR(30) DEFAULT ''")
//            String phone3
//
//            @Column(name = "Email1", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
//            String email1
//
//            @Column(name = "Email2", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
//            String email2
//
//            @Column(name = "NationalTaxRateDesc", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
//            String nationalTaxRateDesc
//
//            @Column(name = "NationalTaxRate", nullable = false, columnDefinition = "DECIMAL(6,5) UNSIGNED")
//            Double nationalTaxRate
//
//            @Column(name = "LocalTaxRateDesc", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
//            String localTaxRateDesc
//
//            @Column(name = "LocalTaxRate", nullable = false, columnDefinition = "DECIMAL(6,5) UNSIGNED")
//            Double localTaxRate
//
//
//            @Column(name = "AgencyID", columnDefinition = "VARCHAR(30) DEFAULT ''")
//            String agencyId
//
//            @Column(name = "CreditRating", columnDefinition = "INT(5) UNSIGNED")
//            Integer creditRating
//
//            @Column(name = "NetWorth", columnDefinition = "INT(10)")
//            Integer netWorth
//
//            @Column(name = "MarketingNameplate", columnDefinition = "VARCHAR(100) DEFAULT ''")
//            String marketingNameplate
//
//            @Column(name = "IsCurrent", columnDefinition = "TINYINT(1) UNSIGNED")
//            boolean isCurrent
//
//            @Column(name = "BatchId", columnDefinition = "INT(5) UNSIGNED")
//            BatchId batchId
//
//            @Column(name = "EffectiveDate", nullable = false, columnDefinition = "DATE")
//            LocalDate effectiveDate
//
//            @Column(name = "EndDate", nullable = false, columnDefinition = "DATE")
//            LocalDate endDate

        }
        dimCompanies
    }

    public static void main(String[] args) {
        def file = new File("/Users/ankushsharma/Downloads/tpc-di-tools/pdgf/output/Batch1/CustomerMgmt.xml")

        def xml = new XmlSlurper()
        def node = xml.parseText(file.text)

        def actions = node.name()

        for(action in node.Action) {
            def customer = action.Customer

            def dob = customer.@C_ID
            println dob

        }

    }

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
        else if(!hasText(areaCode) && hasText(localNum)) {
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

}
