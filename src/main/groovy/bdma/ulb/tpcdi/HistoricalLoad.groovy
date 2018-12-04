package bdma.ulb.tpcdi

import bdma.ulb.tpcdi.domain.*
import bdma.ulb.tpcdi.domain.enums.BatchId
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
import java.util.stream.Collectors

import static bdma.ulb.tpcdi.domain.Constants.*
import static bdma.ulb.tpcdi.util.Strings.NULL

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
            DimSecurityRepository dimSecurityRepository
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


    }


    private static File getFile(List<File> files, String fileName) {
        files.find { it.name == fileName }
    }

    private static List<File> getTxtFilesThatStartWithName(List<File> files, String fileName) {
        files.findAll { it.name.startsWith(fileName) && it.name.endsWith("") } //No format specified
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
                    if(Strings.hasText(jobCode)) {
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
                    def companyName = Strings.hasText(record.substring(18, 78)) ? record.substring(18, 78) : NULL
                    def cik = record.substring(78, 88) as Integer
                    def status = Status.from(statusTypes.find { it.id == record.substring(88, 92)}.name)
                    def industryId = record.substring(92, 94)
                    def spRating = Strings.hasText(record.substring(94, 98)) ? record.substring(94, 98) : NULL
                    def foundingDate = Strings.hasText(record.substring(98, 106)) ? DateTimeUtil.parseFinWireDate(record.substring(98, 106)) : null
                    def addrLine1 = Strings.hasText(record.substring(106, 186)) ? record.substring(106, 186) : NULL
                    def addrLine2 = Strings.hasText(record.substring(186, 266)) ? record.substring(186, 266) : NULL
                    def postalCode = Strings.hasText(record.substring(266, 278)) ? record.substring(266, 278) : NULL
                    def city = Strings.hasText(record.substring(278, 303)) ? record.substring(278, 303) : NULL
                    def stateProv = Strings.hasText(record.substring(303, 323)) ? record.substring(303, 323) : NULL
                    def country = Strings.hasText(record.substring(323, 347)) ? record.substring(323, 347) : NULL
                    def ceo = Strings.hasText(record.substring(347, 393)) ? record.substring(347, 393) : NULL
                    def desc = Strings.hasText(record.substring(393, record.size())) ? record.substring(393, record.size()) : NULL
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
}
