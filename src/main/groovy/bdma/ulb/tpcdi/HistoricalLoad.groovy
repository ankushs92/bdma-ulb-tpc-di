package bdma.ulb.tpcdi

import bdma.ulb.tpcdi.domain.*
import bdma.ulb.tpcdi.domain.enums.BatchId
import bdma.ulb.tpcdi.domain.enums.Gender
import bdma.ulb.tpcdi.domain.enums.Status
import bdma.ulb.tpcdi.domain.enums.TaxStatus
import bdma.ulb.tpcdi.domain.keys.CustomerXmlKeys
import bdma.ulb.tpcdi.domain.keys.DimTimeKeys
import bdma.ulb.tpcdi.domain.keys.ProspectRecordKeys
import bdma.ulb.tpcdi.repository.*
import bdma.ulb.tpcdi.util.DateTimeUtil
import bdma.ulb.tpcdi.util.FileParser
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.LocalDate
import java.time.Month

import static bdma.ulb.tpcdi.domain.Constants.*
import static bdma.ulb.tpcdi.domain.Constants.PIPE_DELIM
import static bdma.ulb.tpcdi.domain.Constants.TRADE_HISTORY_TXT
import static bdma.ulb.tpcdi.util.Strings.*

import static java.util.Objects.nonNull

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
    private final DimTradeRepository dimTradeRepository
    private final FactCashBalanceRepository factCashBalanceRepository
    private final FactHoldingsRepository factHoldingsRepository
    private final ProspectRepository prospectRepository
    private final FactWatchesRepository factWatchesRepository
    private final FactMarketHistoryRepository factMarketHistoryRepository

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
            DimCustomerRepository dimCustomerRepository,
            DimTradeRepository dimTradeRepository,
            FactCashBalanceRepository factCashBalanceRepository,
            FactHoldingsRepository factHoldingsRepository,
            ProspectRepository prospectRepository,
            FactWatchesRepository factWatchesRepository,
            FactMarketHistoryRepository factMarketHistoryRepository
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
        this.dimTradeRepository = dimTradeRepository
        this.factCashBalanceRepository = factCashBalanceRepository
        this.factHoldingsRepository = factHoldingsRepository
        this.prospectRepository = prospectRepository
        this.factWatchesRepository = factWatchesRepository
        this.factMarketHistoryRepository = factMarketHistoryRepository
    }

    @PostConstruct
    void parseFilesAndPerformHistoralLoad() {
        log.info "Preparing to parse all csv and txt files"
        List<File> files = []
        def directory = new File(directoryLocation)
        directory.eachFile { file ->
            files << file
        }

        def batchDateFile = getFile(files, BATCH_DATE_TXT)
        def batchDate = getBatchDate(batchDateFile)
//
        def dimDates = dimDateRepository.findAll()
//        def dimDateFile = getFile(files, DATE_TXT)
//        log.info "Loading data into DimDate"
//        List<DimDate> dimDates = parseDimDate(dimDateFile)
        def earliestDate = dimDates.sort { it.date }.find().date
//        dimDates = dimDateRepository.saveAll(dimDates)

        List<DimTime> dimTimes = dimTimeRepository.findAll()
//        def dimTimeFile = getFile(files, TIME_TXT)
//        log.info "Loading data into DimTime"
//        List<DimTime> dimTimes = parseDimTime(dimTimeFile)
//        dimTimes = dimTimeRepository.saveAll(dimTimes)
//
        List<Industry> industries = industryRepository.findAll()
//        def industryFile = getFile(files, INDUSTRY_TXT)
//        log.info "Loading data into Industry"
//        List<Industry> industries = parseIndustry(industryFile)
//        industryRepository.saveAll(industries)
//
        def statusTypes = statusTypeRepository.findAll()
//        def statusTypeFile = getFile(files, STATUS_TYPE_TXT)
//        log.info "Loading data into StatusType"
//        List<StatusType> statusTypes = parseStatusType(statusTypeFile)
//        statusTypeRepository.saveAll(statusTypes)
//
        List<TaxRate> taxRates = taxRateRepository.findAll()
//        def taxRateFile = getFile(files, TAX_RATE_TXT)
//        log.info "Loading data into TaxRate"
//        List<TaxRate> taxRates = parseTaxRates(taxRateFile)
//        taxRateRepository.saveAll(taxRates)
//
        def tradeTypes = tradeTypeRepository.findAll()
//        def tradeTypeFile = getFile(files, TRADE_TYPE_TXT)
//        log.info "Loading data into TradeType"
//        List<TradeType> tradeTypes = parseTradeTypes(tradeTypeFile)
//        tradeTypeRepository.saveAll(tradeTypes)
//
////        List<DimBroker> dimBrokers = dimBrokerRepository.findAll()
//        def dimBrokerFile = getFile(files, DIM_BROKER_CSV)
//        log.info "Loading data into DimBroker"
//        List<DimBroker> dimBrokers = parseDimBrokers(dimBrokerFile, earliestDate)
//        dimBrokers = dimBrokerRepository.saveAll(dimBrokers)
//
//        log.info "Parsing all FinWire txt files"
        List<DimCompany> dimCompanies = dimCompanyRepository.findAll()
        def finWireFiles = getTxtFilesThatStartWithName(files, FINWIRE_RECORDS_CSV_INITIAL_NAME)
//////        //Since we will be needing them further down the line, we parse all of them in one go and keep them in memory
        def finWireRecords = finWireFiles.collect { file -> file.readLines() }.flatten() as List<String>
//        List<DimCompany> dimCompanies = parseDimCompanies(finWireRecords, industries, statusTypes, earliestDate)
//        log.info "Loading data into DimCompany"
//        dimCompanies = dimCompanyRepository.saveAll(dimCompanies)
//
//        List<DimSecurity> dimSecurities = dimSecurityRepository.findAll()
        List<DimSecurity> dimSecurities = parseDimSecurity(finWireRecords, dimCompanies, statusTypes)
        log.info "Loading data into DimSecurity"
        dimSecurities = dimSecurityRepository.saveAll(dimSecurities)
//
//        List<Financial> financials = financialRepository.findAll()
//        List<Financial> financials = parseFinancials(finWireRecords, dimCompanies)
//        log.info "Loading data into Financial"
//        financials = financialRepository.saveAll(financials)

        //We need prospect file records before DimCustomer, but the data has to be saved after data has been saved for DimCustomer
        def prospectFile = getFile(files, PROSPECT_CSV)
        List<String[]> prospectFileRecords = getProspectFileRecords(prospectFile)

//        List<DimCustomer> dimCustomers = dimCustomerRepository.findAll()
//        def customerXmlFile = getFile(files, CUSTOMER_XML)
//        def customerXmlData = parseCustomerXml(customerXmlFile)
//        List<DimCustomer> dimCustomers = parseDimCustomer(customerXmlData, taxRates, prospectFileRecords)
//        log.info "Loading data into DimCustomer"
//        dimCustomers = dimCustomerRepository.saveAll(dimCustomers)
//
//
//
//        Map<Integer, List<DimBroker>> dimBrokersCache = dimBrokers.groupBy { it.brokerId }
//        Map<Integer, List<DimCustomer>> dimCustomersCache = dimCustomers.groupBy { it.customerId }
//
        List<DimAccount> dimAccounts = dimAccountRepository.findAll()
//        List<DimAccount> dimAccounts = buildDimAccount(customerXmlData, dimCustomers, dimBrokers, dimBrokersCache, dimCustomersCache)
//        log.info "Loading data into DimAccount"
//        dimAccounts = dimAccountRepository.saveAll(dimAccounts)
//
//
         Map<String, List<DimSecurity>> dimSecurityCache = dimSecurities.groupBy { dimSecurity -> dimSecurity.symbol }
        println dimSecurityCache.get("AAAAAAAAAAAADMO")
        println "hsss"
        Map<Integer, List<DimAccount>> dimAccountsCache = dimAccounts.groupBy { dimAccount -> dimAccount.accountId }
          Map<LocalDate, Integer> dimDatesCache = dimDates.collectEntries { dimDate ->
              [(dimDate.date) : dimDate.id]
          }
////
        Map<DimTimeKeys, Integer> dimTimeCache = dimTimes.collectEntries { dimTime ->
            def key = new DimTimeKeys(hourId : dimTime.hourId, minuteId : dimTime.minuteId, secondId : dimTime.secondId)
            [ (key) : dimTime.id]
        }
////
////        List<DimTrade> dimTrades = dimTradeRepository.findAll()
         def tradeFile = getFile(files, TRADE_TXT)
          def tradeHistoryFile = getFile(files, TRADE_HISTORY_TXT)
         def tradeHistoryRecords = getTradeHistoryRecords(tradeHistoryFile)
        List<DimTrade> dimTrades = parseTrade(tradeFile, dimDatesCache, dimTimeCache, statusTypes, tradeTypes, dimSecurityCache, dimAccountsCache, tradeHistoryRecords)
        log.info "Loading data into DimTrades"
        dimTradeRepository.saveAll(dimTrades)
////
//        def factCashBalanceFile = getFile(files, FACT_CASH_BALANCE_TXT)
//        def factCashBalance = parseFactCashBalance(factCashBalanceFile, dimAccountsCache, dimDatesCache)
//        log.info "Loading data into Fact Cash Balance"
//        factCashBalanceRepository.saveAll(factCashBalance)
//
//        Map<Integer, DimTrade> dimTradeCache = dimTrades.collectEntries { dimTrade ->
//            [(dimTrade.tradeId) : dimTrade]
//        }
//        def factHoldingsFile = getFile(files, FACT_HOLDINGS_TXT)
//        List<FactHoldings> factHoldings = parseFactHoldings(factHoldingsFile, dimTradeCache)
//        log.info "Loading data into FactHoldings"
//        factHoldingsRepository.saveAll(factHoldings)
//
//        List<Prospect> prospects = parseProspect(prospectFileRecords, dimCustomers, dimDatesCache, batchDate)
//        log.info "Loading data into Prospects"
//        prospectRepository.saveAll(prospects)

//        def factWatchesFile = getFile(files, FACT_WATCHES_TXT)
//        List<FactWatches> factWatches = parseFactWatches(factWatchesFile, dimCustomersCache, dimSecurityCache, dimDatesCache)
//        log.info "Loading data into FactWatches"
//        factWatchesRepository.saveAll(factWatches)

//        def factMarketHistoryFile = getFile(files, FACT_MARKET_HISTORY_TXT)
//        List<FactMarketHistory> factMarketHistories = parseFactMarketHistory(factMarketHistoryFile, dimSecurityCache, dimDatesCache, financials, dimCompanies)
//        log.info "Saving data into FactMarketHistory"
//        factMarketHistoryRepository.saveAll(factMarketHistories)
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
        List<DimCompany> dimCompanies = []
        records.findAll { record ->
                    //Refer to section 2.2.2.8
                    def recType = record.substring(15, 18)
                    recType == "CMP"
                }
                .collect { record ->
                    def pts = DateTimeUtil.parseFinWireDateAndTime(record.substring(0, 15)).toLocalDate()
                    def companyName = hasText(record.substring(18, 78)) ? record.substring(18, 78) : NULL
                    def cik = record.substring(78, 88) as Integer
                    def status = Status.from(statusTypes.find { it.id == record.substring(88, 92)}.name)
                    def industryId = record.substring(92, 94)
                    def spRating = hasText(record.substring(94, 98)) ? record.substring(94, 98) : null
                    def foundingDate = hasText(record.substring(98, 106)) ? DateTimeUtil.parseFinWireDate(record.substring(98, 106)) : null
                    def addrLine1 = hasText(record.substring(106, 186)) ? record.substring(106, 186) : null
                    def addrLine2 = hasText(record.substring(186, 266)) ? record.substring(186, 266) : null
                    def postalCode = hasText(record.substring(266, 278)) ? record.substring(266, 278) : NULL
                    def city = hasText(record.substring(278, 303)) ? record.substring(278, 303) : NULL
                    def stateProv = hasText(record.substring(303, 323)) ? record.substring(303, 323) : NULL
                    def country = hasText(record.substring(323, 347)) ? record.substring(323, 347) : null
                    def ceo = hasText(record.substring(347, 393)) ? record.substring(347, 393) : NULL
                    def desc = hasText(record.substring(393, record.size())) ? record.substring(393, record.size()) : NULL
                    def industry = industries.find { it.id == industryId }.name

                    boolean isLowGrade = !(spRating.startsWith("A") || spRating.startsWith("BBB"))

                    def effectiveDate = pts
                    def oldCompanyRecord = dimCompanies.find { it.isCurrent && it.companyId == cik }
                    if(oldCompanyRecord) {
                        oldCompanyRecord.isCurrent = false
                        oldCompanyRecord.endDate = effectiveDate
                    }
                    def dimCompany = new DimCompany(
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
                        effectiveDate : effectiveDate,
                        endDate : LocalDate.of(9999, Month.DECEMBER, 31),
                        batchId : BatchId.HISTORICAL_LOAD
                    )
                    dimCompanies << dimCompany
        }
        dimCompanies
    }


    private static List<DimSecurity> parseDimSecurity(
            List<String> records,
            List<DimCompany> dimCompanies,
            List<StatusType> statusTypes
    )
    {
        List<DimSecurity> dimSecurities = []
        records.findAll { record ->
                    def recType = record.substring(15,18)
                    return recType == "SEC"
                }
                .each{ record ->
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
                    def security = "security"
                    def secEffectiveDate = pts
                    def endDate = LocalDate.of(9999, Month.DECEMBER, 31)
                    def batchId = BatchId.HISTORICAL_LOAD
                    def skCompanyIds
                    if(coNameOrCik.size() == 10) {
                        skCompanyIds = dimCompanies.findAll { company ->
                            company.companyId == (coNameOrCik as Integer)
                        }.id

                    }
                    else {
                        skCompanyIds = dimCompanies.findAll { company ->
                            company.name == coNameOrCik
                        }.id
                    }
                    skCompanyIds.each { skCompId ->
                        def comp = dimCompanies.find { it.id == skCompId }
                        def compEffectivDate = comp.effectiveDate
                        def compEndDate = comp.endDate
                        def isCompCurrent = comp.isCurrent
                        def oldSecurityItem = dimSecurities.find { it.isCurrent &&  it.symbol == symbol }
                        if(oldSecurityItem) {
                            oldSecurityItem.isCurrent = false
                            oldSecurityItem.endDate = secEffectiveDate
                        }

                        if(!isCompCurrent) {
                            def dimSecurity = new DimSecurity(
                                    symbol : symbol,
                                    issue : issueType,
                                    status : status,
                                    name : name,
                                    exchangeId : exchangeId,
                                    security : security,
                                    skCompanyId : skCompId,
                                    sharesOutstanding : sharesOutstanding,
                                    firstDate : firstDate,
                                    firstTradeOnExchange : firstTradeOnExchange,
                                    dividend : dividend,
                                    isCurrent : isCompCurrent,
                                    effectiveDate : compEffectivDate,
                                    endDate : compEndDate,
                                    batchId : batchId
                            )
                            dimSecurities << dimSecurity
                        }
                        else {
                            if((secEffectiveDate.isAfter(compEffectivDate) || compEffectivDate == secEffectiveDate)  && (secEffectiveDate.isBefore(compEndDate))) {
                                def dimSecurity = new DimSecurity(
                                        symbol : symbol,
                                        issue : issueType,
                                        status : status,
                                        name : name,
                                        exchangeId : exchangeId,
                                        security : security,
                                        skCompanyId : skCompId,
                                        sharesOutstanding : sharesOutstanding,
                                        firstDate : firstDate,
                                        firstTradeOnExchange : firstTradeOnExchange,
                                        dividend : dividend,
                                        isCurrent : true,
                                        effectiveDate : secEffectiveDate,
                                        endDate : endDate,
                                        batchId : batchId
                                )
                                dimSecurities << dimSecurity
                            }
                        }

                    }
             }
        dimSecurities
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
                    def company = dimCompanies.find { it.id == skCompanyId }
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
                            fiOutDilut : dilutedShOut,
                            companyId : company.companyId
                    )


        }
    }

    private static GPathResult parseCustomerXml(File file) {
         new XmlSlurper().parseText(file.text)
    }

    private static List<DimCustomer> parseDimCustomer(
            GPathResult xmlData,
            List<TaxRate> taxRates,
            List<String[]> prospectRecords
    )
    {
        //Parse customer management xml
        List<DimCustomer> dimCustomers = []
        Map<ProspectRecordKeys, String[]> prospectCache = prospectRecords.collectEntries { String[] prospectRecord ->
            String prospectLastName = prospectRecord[1]
            String prospectFirstName = prospectRecord[2]
            String prospectAddressline1 = prospectRecord[5]
            String prospectAddressline2 = prospectRecord[6]
            String prospectPostalcode = prospectRecord[7]
            [(new ProspectRecordKeys(
                    firstName : toLowerCaseIfAnyMixedCase(prospectFirstName),
                    lastName : toLowerCaseIfAnyMixedCase(prospectLastName),
                    addressline1 : toLowerCaseIfAnyMixedCase(prospectAddressline1),
                    addressline2 : toLowerCaseIfAnyMixedCase(prospectAddressline2),
                    postalCode : toLowerCaseIfAnyMixedCase(prospectPostalcode)
            )), prospectRecord]
        }

        Map<CustomerXmlKeys, Object> xmlUpdateOrInactCache = [:]
        xmlData.Action.each { action ->
            def actionType = action.@ActionType as String
            def customer = action.Customer
            def customerId = customer.@C_ID?.toInteger()
            if(actionType == "UPDCUST" || actionType == "INACT") {
                xmlUpdateOrInactCache.put(new CustomerXmlKeys(actionType : actionType, customerId : customerId), action)
            }
        }

        xmlData.Action.each { action ->
            def actionTimestamp = DateTimeUtil.parseIso(action.@ActionTS as String)
            def actionType = action.@ActionType as String
            def customer = action.Customer
            def customerId = customer.@C_ID?.toInteger()
            def taxId = customer.@C_TAX_ID as String
            def gender = Gender.from(customer.@C_GNDR as String)
            def tier = customer.@C_TIER?.toInteger() as Integer
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
            def nationalTaxRate = taxRates.find { it.id == nationalTaxId }

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

            boolean customerHasUpdateAction = false
            boolean customerHasInactAction = false

            if(customerUpdateAction) customerHasUpdateAction = true
            if(customerInactAction) customerHasInactAction = true

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

            def status = Status.ACTIVE
            def dimCustomer
            if(actionType == "NEW") {
                status = Status.ACTIVE
                dimCustomer = new DimCustomer(
                        customerId : customerId,
                        taxId : taxId,
                        status : status,
                        firstName : firstName,
                        lastName : hasText(lastName) ? lastName : "", // -> TODO : This is a hack
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
                        endDate : endDate,
                        actionType : actionType
                )

            }
            else if (actionType == "INACT") {
                def expiredCustomer = getLastCustomer(dimCustomers, customerId)
                expiredCustomer.isCurrent = false
                expiredCustomer.endDate = effectiveDate


                if(customerId == 31) {
                    println "Printing original tax id " + taxId
                    println "INACT UPDCUST " + expiredCustomer
                }

                dimCustomer = new DimCustomer(
                        status : Status.INACTIVE,
                        customerId : customerId ,
                        taxId : hasText(taxId) ? taxId : expiredCustomer.taxId,
                        firstName : nonNull(firstName) ? firstName : expiredCustomer.firstName,
                        lastName : nonNull(lastName) ? lastName : expiredCustomer.lastName,
                        middleInitial : nonNull(middleName) ? middleName : expiredCustomer.middleInitial,
                        gender : nonNull(gender) ? gender : expiredCustomer.gender,
                        tier : nonNull(tier) ? tier : expiredCustomer.tier,
                        dob : nonNull(dob) ? dob : expiredCustomer.dob,
                        addressLine1 : nonNull(addressLine1) ? addressLine1 : expiredCustomer.addressLine1,
                        addressLine2 : nonNull(addressLine2) ? addressLine1 : expiredCustomer.addressLine2,
                        postalCode : nonNull(postalCode) ? postalCode : expiredCustomer.postalCode,
                        city : nonNull(postalCode) ? city : expiredCustomer.city,
                        stateProv : nonNull(stateProv) ? stateProv : expiredCustomer.stateProv,
                        country : nonNull(country) ? country : expiredCustomer.country,
                        phone1 : nonNull(phone1) ? phone1 : expiredCustomer.phone1,
                        phone2 : nonNull(phone2) ? phone2 : expiredCustomer.phone2,
                        phone3 : nonNull(phone3) ? phone3 : expiredCustomer.phone3,
                        email1 : hasText(email1) ? email1 : expiredCustomer.email1,
                        email2 : hasText(email2) ? email2 : expiredCustomer.email2,
                        nationalTaxRateDesc : nonNull(nationalTaxRateDesc) ? nationalTaxRateDesc : expiredCustomer.nationalTaxRateDesc,
                        nationalTaxRate : nonNull(nationalTaxRateValue) ? nationalTaxRateValue : expiredCustomer.nationalTaxRate,
                        localTaxRateDesc : nonNull(localTaxRateDesc) ? localTaxRateDesc : expiredCustomer.localTaxRateDesc,
                        localTaxRate : nonNull(localTaxRateValue) ? localTaxRateValue : expiredCustomer.localTaxRate,
                        agencyId : nonNull(agencyId) ? agencyId : expiredCustomer.agencyId,
                        creditRating : nonNull(creditRating) ? creditRating : expiredCustomer.creditRating,
                        netWorth : nonNull(netWorth) ? netWorth : expiredCustomer.netWorth,
                        marketingNameplate : nonNull(marketingNameplate) ? marketingNameplate : expiredCustomer.marketingNameplate,
                        batchId : batchId,
                        isCurrent : isCurrent,
                        effectiveDate : effectiveDate,
                        endDate : endDate,
                        actionType : actionType
                )
            }


            if(actionType == "UPDCUST") {
                def expiredCustomer = getLastCustomer(dimCustomers, customerId)
                expiredCustomer.isCurrent = false
                expiredCustomer.endDate = effectiveDate
                if(customerId == 31) {
                    println "Printing original tax id " + taxId
                    println "ACTION UPDCUST " + expiredCustomer
                }


                dimCustomer = new DimCustomer(
                        status : nonNull(status) ? status : expiredCustomer.status,
                        customerId : customerId ,
                        taxId : hasText(taxId) ? taxId : expiredCustomer.taxId,
                        firstName : nonNull(firstName) ? firstName : expiredCustomer.firstName,
                        lastName : nonNull(lastName) ? lastName : expiredCustomer.lastName,
                        middleInitial : nonNull(middleName) ? middleName : expiredCustomer.middleInitial,
                        gender : nonNull(gender) ? gender : expiredCustomer.gender,
                        tier : nonNull(tier) ? tier : expiredCustomer.tier,
                        dob : nonNull(dob) ? dob : expiredCustomer.dob,
                        addressLine1 : nonNull(addressLine1) ? addressLine1 : expiredCustomer.addressLine1,
                        addressLine2 : nonNull(addressLine2) ? addressLine1 : expiredCustomer.addressLine2,
                        postalCode : nonNull(postalCode) ? postalCode : expiredCustomer.postalCode,
                        city : nonNull(postalCode) ? city : expiredCustomer.city,
                        stateProv : nonNull(stateProv) ? stateProv : expiredCustomer.stateProv,
                        country : nonNull(country) ? country : expiredCustomer.country,
                        phone1 : nonNull(phone1) ? phone1 : expiredCustomer.phone1,
                        phone2 : nonNull(phone2) ? phone2 : expiredCustomer.phone2,
                        phone3 : nonNull(phone3) ? phone3 : expiredCustomer.phone3,
                        email1 : hasText(email1) ? email1 : expiredCustomer.email1,
                        email2 : hasText(email2) ? email2 : expiredCustomer.email2,
                        nationalTaxRateDesc : nonNull(nationalTaxRateDesc) ? nationalTaxRateDesc : expiredCustomer.nationalTaxRateDesc,
                        nationalTaxRate : nonNull(nationalTaxRateValue) ? nationalTaxRateValue : expiredCustomer.nationalTaxRate,
                        localTaxRateDesc : nonNull(localTaxRateDesc) ? localTaxRateDesc : expiredCustomer.localTaxRateDesc,
                        localTaxRate : nonNull(localTaxRateValue) ? localTaxRateValue : expiredCustomer.localTaxRate,
                        agencyId : nonNull(agencyId) ? agencyId : expiredCustomer.agencyId,
                        creditRating : nonNull(creditRating) ? creditRating : expiredCustomer.creditRating,
                        netWorth : nonNull(netWorth) ? netWorth : expiredCustomer.netWorth,
                        marketingNameplate : nonNull(marketingNameplate) ? marketingNameplate : expiredCustomer.marketingNameplate,
                        batchId : batchId,
                        isCurrent : isCurrent,
                        effectiveDate : effectiveDate,
                        endDate : endDate,
                        actionType : actionType
                )
            }

            if(actionType in ["NEW", "UPDCUST", "INACT"]) {
                dimCustomers << dimCustomer
            }
        }
        dimCustomers.grep()
    }


    private static List<DimAccount> buildDimAccount(
            GPathResult xmlData,
            List<DimCustomer> customers,
            List<DimBroker> brokers,
            Map<Integer, List<DimBroker>> brokersCache,
            Map<Integer, List<DimCustomer>> dimCustomersCache
    )
    {
        List<DimAccount> dimAccounts = []


        Map<Integer, Object> newOrAddAccActionCache = [:]
        xmlData.Action.each { action ->
            def actionType = action.@ActionType as String
            def customer = action.Customer
            def account = customer.Account
            def accountId = account.@CA_ID?.toInteger()
            if(actionType == "NEW" || actionType == "ADDACCT") {
                newOrAddAccActionCache.put(accountId, action.Customer)
            }
        }
        xmlData.Action.each { action ->
            def actionType = action.@ActionType as String
            def customerXml = action.Customer
            def customerId = customerXml.@C_ID?.toInteger()
            def actionTimestamp = DateTimeUtil.parseIso(action.@ActionTS as String)

            def account = customerXml.Account
            def accountId = account.@CA_ID?.toInteger()
            def taxStatusInt = account.@CA_TAX_ST?.toInteger()
            def brokerId = account.CA_B_ID?.toInteger()
            def accountDesc = account.CA_NAME

            def isCurrent = true
            def batchId = BatchId.HISTORICAL_LOAD
            def effectiveDate = actionTimestamp.toLocalDate()
            def endDate = LocalDate.of(9999, Month.DECEMBER, 31)

            def taxStatus = TaxStatus.from(taxStatusInt)

            def status = Status.ACTIVE
            def skBrokerId, skCustomerId
            def actionDate = actionTimestamp.toLocalDate()
            def dimAccount
//            println "Actyon Tyope " + actionType + ", accountId " + accountId + " AND CUSTOMER ID IS " + customerId + ", time " + actionTimestamp.truncatedTo(ChronoUnit.SECONDS)

            if(actionType == "NEW" ) {
                status = Status.ACTIVE
                skBrokerId = brokersCache.get(brokerId).find {
                    def brokerEffectiveDate = it.effectiveDate
                    def brokerEndDate = it.endDate
                    (actionDate == brokerEffectiveDate || actionDate.isAfter(brokerEffectiveDate)) && (actionDate.isBefore(brokerEndDate) || actionDate == brokerEndDate )
                }.id

                skCustomerId = dimCustomersCache.get(customerId).find {
                    def customersEffectiveDate = it.effectiveDate
                    def customersEndDate = it.endDate
                    (actionDate == customersEffectiveDate || actionDate.isAfter(customersEffectiveDate)) && (actionDate.isBefore(customersEndDate) || actionDate == customersEndDate )
                }.id
                dimAccount = new DimAccount(
                        accountId : accountId,
                        skBrokerId : skBrokerId,
                        skCustomerId : skCustomerId,
                        status : status,
                        accountDesc : accountDesc,
                        taxStatus : taxStatus,
                        isCurrent : isCurrent,
                        batchId : batchId,
                        effectiveDate : effectiveDate,
                        endDate : endDate,
                        actionType : actionType
                )
            }

            if(actionType == "UPDACCT" ) {
                def expiredAcc = getLastAccount(dimAccounts, accountId)
                expiredAcc.isCurrent = false
                expiredAcc.endDate = effectiveDate

                dimAccount = new DimAccount(
                        accountId : accountId,
                        skBrokerId : nonNull(brokerId) ? getBrokerSk(brokers, brokerId) : expiredAcc.skBrokerId,
                        skCustomerId : nonNull(customerId) ? getLastCustomer(customers, customerId).id : expiredAcc.skCustomerId,
                        status : status,
                        accountDesc : nonNull(accountDesc) ? accountDesc : expiredAcc.accountDesc,
                        taxStatus : taxStatus,
                        isCurrent : isCurrent,
                        batchId : batchId,
                        effectiveDate : effectiveDate,
                        endDate : endDate,
                        actionType : actionType
                )
            }

            if(actionType == "CLOSEACCT") {
                def expiredAcc = getLastAccount(dimAccounts, accountId)
                expiredAcc.isCurrent = false
                expiredAcc.endDate = effectiveDate

                dimAccount = new DimAccount(
                        accountId : accountId,
                        skBrokerId : expiredAcc.skBrokerId,
                        skCustomerId : expiredAcc.skCustomerId,
                        status : Status.INACTIVE,
                        accountDesc : accountDesc,
                        taxStatus : taxStatus,
                        isCurrent : isCurrent,
                        batchId : batchId,
                        effectiveDate : effectiveDate,
                        endDate : endDate,
                        actionType : actionType
                )
            }

            if(actionType == "UPDCUST") {
                def lastCustomer = getLastCustomer(customers, customerId)
                dimAccounts.grep().findAll { acc ->
                                acc.customerId == customerId
                            }
                            .each { acc ->
                                acc.skCustomerId = lastCustomer.id
                            }

            }

            if(actionType == "INACT") {
                def inactiveCustomer = getLastCustomer(customers, customerId)
                def currentAccForThisGuy = dimAccounts.grep().findAll { it.customerId == customerId && isCurrent }
                currentAccForThisGuy.each { acc ->
                    acc.isCurrent = false
                    acc.endDate = effectiveDate
                    acc.skCustomerId = inactiveCustomer.id
                    acc.status = Status.INACTIVE
                }
            }

            dimAccounts << dimAccount
        }
        dimAccounts.grep()
    }

    private static List<DimTrade> parseTrade(
            File file,
            Map<LocalDate, Integer> dimDatesCache,
            Map<DimTimeKeys, Integer> dimTimeCache,
            List<StatusType> statusTypes,
            List<TradeType> tradeTypes,
            Map<String, List<DimSecurity>> dimSecurityCache,
            Map<Integer, List<DimAccount>> dimAccountsCache,
            List<String[]> tradeHistoryRecords
    )
    {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        Map<String, String[]> tradeHistoryCache = tradeHistoryRecords.collectEntries { histRecord ->
            [(histRecord[0]) : histRecord]
        }
        List<DimTrade> dimTrades = []
        records.each { String[] tradeRecord ->
            def tidString = tradeRecord[0]
            def tid = tidString as Integer
            def tradeTimestamp = DateTimeUtil.parseIso(tradeRecord[1])
            def statusTypeId = tradeRecord[2]
            def tradeTypeId = tradeRecord[3]
            boolean isCash = tradeRecord[4] == "1"
            def securitySymbol = tradeRecord[5]
            def quantity = tradeRecord[6] as Integer
            def bidPrice = tradeRecord[7] as Double
            def customerAccId = tradeRecord[8] as Integer
            def execName = tradeRecord[9]
            def tradePrice = hasText(tradeRecord[10]) ? tradeRecord[10] as Double : null
            def tradeCharge = hasText(tradeRecord[11]) ? tradeRecord[11]  as Double : null
            def commission = hasText(tradeRecord[12]) ? tradeRecord[12] as Double : null
            def tax = hasText(tradeRecord[13]) ? tradeRecord[13] as Double : null
            def tradeDate = tradeTimestamp.toLocalDate()
            def tradeTime = tradeTimestamp.toLocalTime()

            tradeHistoryCache.get(tidString).each { record ->
                statusTypeId = tradeRecord[2]
            }

            def skCreateDateId, skCreateTimeId, skCloseDateId, skCloseTimeId
            if(
            (statusTypeId == "SBMT" && (tradeTypeId == "TMB" || tradeTypeId == "TMS"))
                || statusTypeId == "PNDG"
            )
            {
                skCreateDateId = dimDatesCache.get(tradeDate)
                skCreateTimeId = dimTimeCache.get(new DimTimeKeys(hourId : tradeTime.hour, minuteId : tradeTime.minute, secondId : tradeTime.second))
            }

            else if(statusTypeId == "CMPT" || statusTypeId == "CNCL") {
                //TODO : DO THIS
                skCreateDateId = dimDatesCache.get(tradeDate)
                skCreateTimeId = dimTimeCache.get(new DimTimeKeys(hourId : tradeTime.hour, minuteId : tradeTime.minute, secondId : tradeTime.second))

                skCloseDateId = dimDatesCache.get(tradeDate)
                skCloseTimeId = dimTimeCache.get(new DimTimeKeys(hourId : tradeTime.hour, minuteId : tradeTime.minute, secondId : tradeTime.second))
            }
            def status = statusTypes.find { it.id == statusTypeId }
            def type = tradeTypes.find { it.id == tradeTypeId }

            println securitySymbol
            println tradeDate
            def security = dimSecurityCache.get(securitySymbol).find { security ->
                def secEffectiveDate = security.effectiveDate
                def secEndDate = security.endDate
                (tradeDate == secEffectiveDate || tradeDate.isAfter(secEffectiveDate))  && (tradeDate.isBefore(secEndDate) || tradeDate == secEndDate)
            }

            def account = dimAccountsCache.get(customerAccId).find { account ->
                def accEffectiveDate = account.effectiveDate
                def accEndDate = account.endDate
                (tradeDate == accEffectiveDate || tradeDate.isAfter(accEffectiveDate))  && (tradeDate.isBefore(accEndDate) || tradeDate == accEndDate)
            }
            def skSecurityId = security.id
            def skCompanyId = security.skCompanyId

            def batchId = BatchId.HISTORICAL_LOAD

            def skBrokerId = account.skBrokerId
            def skCustomerId = account.skCustomerId
            def skAccountId = account.id

            dimTrades << new DimTrade(
                    tradeId : tid,
                    skBrokerId : skBrokerId,
                    skCreateDateId : skCreateDateId,
                    skCreateTimeId : skCreateTimeId,
                    skCloseDateId : skCloseDateId,
                    skCloseTimeId : skCloseTimeId,
                    status : status.name,
                    type : type.name,
                    isCash : isCash,
                    skSecurityId : skSecurityId,
                    skCompanyId : skCompanyId,
                    quantity : quantity,
                    bidPrice : bidPrice,
                    skCustomerId : skCustomerId,
                    skAccountId : skAccountId,
                    executedBy : execName,
                    tradePrice : tradePrice,
                    fee : tradeCharge,
                    commission : commission,
                    tax : tax,
                    batchId : batchId
            )
        }
        dimTrades
    }


    private static List<FactCashBalance> parseFactCashBalance(
            File file,
            Map<Integer, List<DimAccount>> dimAccountsCache,
            Map<LocalDate, Integer> dimDatesCache
    )
    {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        def factCashBalances = []

        records.each { record ->
            def accountId = record[0] as Integer
            def timestamp = DateTimeUtil.parseIso(record[1])

            def factDate = timestamp.toLocalDate()
            def account = dimAccountsCache.get(accountId).find { account ->
                def accEffectiveDate = account.effectiveDate
                def accEndDate = account.endDate
                (factDate.isAfter(accEffectiveDate) || factDate == accEffectiveDate) &&  (factDate.isBefore(accEndDate) || factDate == accEndDate)
            }

            def skCustomerId = account.skCustomerId
            def skAccountId = account.id
            def skDateId = dimDatesCache.get(factDate)
            def batchId = BatchId.HISTORICAL_LOAD

            def cash = 0.0
            factCashBalances << new FactCashBalance(
                    skCustomerId : skCustomerId,
                    skAccountId : skAccountId,
                    skDateId : skDateId,
                    cash : cash,
                    batchId : batchId
            )
        }
        factCashBalances
    }


    private static List<FactHoldings> parseFactHoldings(
        File file,
        Map<Integer, DimTrade> dimTradeCache
    )
    {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        List<FactHoldings> factHoldings = []
        records.each { record ->
            def originalTradeId = record[0] as Integer
            def currentTradeId = record[1] as Integer
            def quantityBeforeUpdate = record[2] as Double
            def currentHolding = record[3] as Double
            def dimTrade = dimTradeCache.get(currentTradeId)

            factHoldings << new FactHoldings(
                    tradeId : originalTradeId,
                    currentTradeId : currentTradeId,
                    skCustomerId : dimTrade.skCustomerId,
                    skAccountId : dimTrade.skAccountId,
                    skSecurityId : dimTrade.skSecurityId,
                    skCompanyId : dimTrade.skCompanyId,
                    skDateId : dimTrade.skCloseDateId,
                    skTimeId : dimTrade.skCloseTimeId,
                    currentHolding : currentHolding,
                    currentPrice : quantityBeforeUpdate,
                    batchId : BatchId.HISTORICAL_LOAD,
            )
        }
        factHoldings
    }



    private static List<Prospect> parseProspect(
            List<String[]> prospectRecords,
            List<DimCustomer> dimCustomers,
            Map<LocalDate, Integer> dimDatesCache,
            LocalDate batchDate
    )
    {
        List<Prospect> prospects = []
        prospectRecords.each { prospectRecord ->
            def lastName = prospectRecord[1]
            def firstName = prospectRecord[2]
            def middleInitial = prospectRecord[3]
            def gender = Gender.from(prospectRecord[4])
            def addrLine1 = prospectRecord[5]
            def addrLine2 = prospectRecord[6]
            def postalCode = prospectRecord[7]
            def city = prospectRecord[8]
            def state = prospectRecord[9]
            def country = prospectRecord[10]
            def phone = prospectRecord[11]
            def income = prospectRecord[12]
            def numberOfCars = prospectRecord[13]
            def numberOfChildren = prospectRecord[14]
            def maritalStatus = prospectRecord[15]
            def age = prospectRecord[16]
            def creditRating = prospectRecord[17]
            def ownOrRentFlag = prospectRecord[18]
            def employerName = prospectRecord[19]
            def numCreditCards = prospectRecord[20]
            def netWorth = prospectRecord[21]


            def skRecordDateId = dimDatesCache.get(batchDate)
            def skUpdateDateId = skRecordDateId

            def isCustomer = dimCustomers.any { customer ->
                customer.isCurrent &&
                        customer.firstName.equalsIgnoreCase(firstName) &&
                        customer.lastName.equalsIgnoreCase(lastName) &&
                        customer.addressLine1.equalsIgnoreCase(addrLine1) &&
                        customer.addressLine2.equalsIgnoreCase(addrLine2) &&
                        customer.postalCode.equalsIgnoreCase(postalCode) &&
                        customer.status == Status.ACTIVE
            }

            def marketingNameplate = buildMarketingNameplate(prospectRecord)
            def batchId = BatchId.HISTORICAL_LOAD

            prospects << new Prospect(
                    skRecordDateId : skRecordDateId,
                    skUpdateDateId : skUpdateDateId,
                    batchId : batchId,
                    isCustomer : isCustomer,
                    firstName : firstName,
                    lastName : lastName,
                    middleInitial : middleInitial,
                    gender : gender,
                    addressLine1 : addrLine1,
                    addressLine2 : addrLine2,
                    postalCode : postalCode,
                    city : city,
                    country : country,
                    state : state,
                    income : hasText(income) ? income as Double : null,
                    numberOfCars : hasText(numberOfCars) ? numberOfCars as Integer : null,
                    numberOfChildren : hasText(numberOfChildren) ? numberOfChildren as Integer : null,
                    maritalStatus : maritalStatus,
                    age : hasText(age) ? age as Integer : null,
                    creditRating : hasText(creditRating) ? creditRating as Integer : null,
                    ownOrRentFlag : ownOrRentFlag,
                    numberOfCreditCards : hasText(numCreditCards) ? numCreditCards as Integer : null,
                    netWorth : hasText(netWorth) ? netWorth as Double : null,
                    marketingNameplate : marketingNameplate,
                    phone : phone,
                    employer : employerName
            )
        }
        prospects
    }

    private static List<FactWatches> parseFactWatches(
            File file,
            Map<Integer, List<DimCustomer>> dimCustomersCache,
            Map<String, List<DimSecurity>> dimSecurityCache,
            Map<LocalDate, Integer> dimDateCache
    )
    {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        records.collect { record ->
            def customerId = record[0] as Integer
            def securitySymbol = record[1]
            def timestamp = DateTimeUtil.parseIso(record[2])
            def action = record[3]
            def batchId = BatchId.HISTORICAL_LOAD

            def skDatePlacedId, skDateRemovedId
            if(action == "ACTV") {
                skDatePlacedId  = dimDateCache.get(timestamp.toLocalDate())
            }
            else if (action == "CNCL") {
                skDateRemovedId = dimDateCache.get(timestamp.toLocalDate())
                skDatePlacedId  = dimDateCache.get(timestamp.toLocalDate())
            }
            def customer = dimCustomersCache.get(customerId).find { it.isCurrent }
            def security = dimSecurityCache.get(securitySymbol).find { it.isCurrent }

            def factWatches = new FactWatches(
                    skCustomerId : customer.id,
                    skSecurityId : security.id,
                    skDatePlacedId : skDatePlacedId,
                    skDateRemovedId : skDateRemovedId,
                    batchId : batchId
            )
            factWatches
        }
    }

    private static List<FactMarketHistory> parseFactMarketHistory(
            File file,
            Map<String, List<DimSecurity>> dimSecurityCaches,
            Map<LocalDate, Integer> dimDatesCache,
            List<Financial> financialList,
            List<DimCompany> dimCompanies
    )
    {
        def records = FileParser.parse(file.path, PIPE_DELIM)
        def factMarketHistoryCache = records.groupBy { it[1] }
        Map<Integer, List<Financial>> financialsCache =  financialList.groupBy { it.companyId }
        Map<Integer, Integer> dimCompanyCache = dimCompanies.collectEntries { company ->
            [(company.id) : company.companyId]
        }
        int count = 0
        List<FactMarketHistory> factMarketHistories = []
        for(record in records) {
            count ++
            if(count % 1000 == 0) {
                println count
            }

            def lastCompletedTradingDay = DateTimeUtil.parse(record[0])
            def securitySymbol = record[1]
            def closingPrice = record[2] as Double
            def highestPriceOnDay = record[3] as Double
            def lowestPriceOnDay = record[4] as Double
            def volume = record[5] as Integer

            def securities = dimSecurityCaches.get(securitySymbol)
            def security = securities.find {
                def firstSecurityDate = it.firstDate
                def securityEndDate = it.endDate
                it.isCurrent &&
                        lastCompletedTradingDay.isAfter(firstSecurityDate)  && lastCompletedTradingDay.isBefore(securityEndDate)
            }

            def skSecurityId = security.id
            def skCompanyId = security.skCompanyId
            def dateId = dimDatesCache.get(lastCompletedTradingDay)

            def securitiesFromFactMarket = factMarketHistoryCache.get(securitySymbol)
            def priceAndDate = securitiesFromFactMarket.findAll {
                def currentSecFirstDate = DateTimeUtil.parse(record[0])
                (lastCompletedTradingDay.minusWeeks(52).plusDays(1).isAfter(currentSecFirstDate)) || lastCompletedTradingDay == currentSecFirstDate
            }
            .sort { it[3] as Double }

            def highestPriceAndDate = priceAndDate[priceAndDate.size() -1]
            def highestPrice = highestPriceAndDate[3] as Double
//            println "highestPrice for security symbol " + securitySymbol + "is " + highestPrice

            def dateOfHighestPrice = DateTimeUtil.parse(highestPriceAndDate[0])
            def skOfDateOfHighestPriceId = dimDatesCache.get(dateOfHighestPrice)

            def lowestPriceAndDate = priceAndDate[0]
            def lowestPrice = lowestPriceAndDate[3] as Double
//            println "lowestPrice for security symbol " + securitySymbol + "is " + lowestPrice

            def dateOfLowestPrice = DateTimeUtil.parse(lowestPriceAndDate[0])

            def skOfDateOfLowestPriceId = dimDatesCache.get(dateOfLowestPrice)

            def batchId = BatchId.HISTORICAL_LOAD
            def yield = (security.dividend / closingPrice) * 100

            def companyId = dimCompanyCache.get(skCompanyId)
            def financials = financialsCache.get(companyId)
            def financialsOverLast4Quarters = financials.findAll { financial ->
                def startDate = financial.startDate
                startDate.isAfter(lastCompletedTradingDay.minusWeeks(52))
            }
            .sort { it.startDate }
            .reverse()
            .take(4)


            def epsOverLast4Qtrs = financialsOverLast4Quarters.sum { it.fiBasicEps } as Double
            Double peRatio
            try {
                peRatio = closingPrice / epsOverLast4Qtrs
            }
            catch (ex) {

            }
            factMarketHistories << new FactMarketHistory(
                    skSecurityId : skSecurityId,
                    batchId : batchId,
                    skCompanyId : skCompanyId,
                    skDateId : dateId,
                    peRatio : peRatio,
                    fiftyTwoWeekHigh : highestPrice,
                    fiftyTwoWeekLow : lowestPrice,
                    yield : yield,
                    skFiftyTwoWeekHighDateId : skOfDateOfHighestPriceId,
                    skFiftyTwoWeekLowDateId : skOfDateOfLowestPriceId,
                    closePrice : closingPrice,
                    dayHigh : highestPriceOnDay,
                    dayLow : lowestPriceOnDay,
                    volume : volume,
            )
        }
        factMarketHistories
    }

    private void updateAccountIfCustomerInactive(
            List<DimCustomer> dimCustomers,
            List<DimAccount> dimAccounts
    )
    {
        dimCustomers.each { dimCustomer ->
            def compStatus =  dimCustomer.status
            if(compStatus == Status.INACTIVE) {
                def companyAccounts = dimAccounts.findAll { it.skCustomerId == dimCustomer.id }
                companyAccounts.each { companyAcc ->
                    companyAcc.status = Status.INACTIVE
                    dimAccountRepository.save(companyAcc)
                }
            }
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
            tags << "MoneyAlert"
        }

        if((hasText(numOfCarsStr) && (numOfCarsStr as Integer) > 3) || (hasText(numCreditCardsStr) && (numCreditCardsStr as Integer) > 7)) {
            tags << "Spender"
        }

        if(hasText(ageStr) && (ageStr as Integer) < 25 && hasText(netWorthStr) && (netWorthStr as Integer) > 1000000) {
            tags << "Inherited"
        }
        tags.join("+")
    }

    private static List<String[]> getTradeHistoryRecords(File file) {
        FileParser.parse(file.path, PIPE_DELIM)
    }
    private static DimAccount getLastAccount(List<DimAccount> dimAccounts, Integer accountId) {
        dimAccounts.grep().find { it.accountId == accountId && it.isCurrent  }
    }

    private static DimCustomer getLastCustomer(List<DimCustomer> dimCustomers, Integer customerId) {
        dimCustomers.grep().find { it.customerId == customerId && it.isCurrent  }
    }

    private static Integer getBrokerSk(List<DimBroker> brokers, Integer brokerId) {
        brokers.find { it.brokerId == brokerId }.id
    }

    private static LocalDate getBatchDate(File file) {
        DateTimeUtil.parse(file.readLines()[0])
    }

}
