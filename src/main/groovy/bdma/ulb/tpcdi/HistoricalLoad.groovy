package bdma.ulb.tpcdi

import bdma.ulb.tpcdi.domain.DimDate
import bdma.ulb.tpcdi.domain.DimTime
import bdma.ulb.tpcdi.domain.Industry
import bdma.ulb.tpcdi.domain.StatusType
import bdma.ulb.tpcdi.domain.TaxRate
import bdma.ulb.tpcdi.repository.DimAccountRepository
import bdma.ulb.tpcdi.repository.DimDateRepository
import bdma.ulb.tpcdi.repository.DimTimeRepository
import bdma.ulb.tpcdi.repository.IndustryRepository
import bdma.ulb.tpcdi.repository.StatusTypeRepository
import bdma.ulb.tpcdi.repository.TaxRateRepository
import bdma.ulb.tpcdi.util.DateTimeUtil
import bdma.ulb.tpcdi.util.FileParser
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

import static bdma.ulb.tpcdi.domain.Constants.*
import static bdma.ulb.tpcdi.domain.Constants.STATUS_TYPE_TXT
import static bdma.ulb.tpcdi.domain.Constants.TAX_RATE_TXT

@Component
@Slf4j
class HistoricalLoad {


    private final String directoryLocation
    private final ResourceLoader resourceLoader

    private final DimAccountRepository dimAccountRepository
    private final DimDateRepository dimDateRepository
    private final DimTimeRepository dimTimeRepository
    private final IndustryRepository industryRepository
    private final StatusTypeRepository statusTypeRepository
    private final TaxRateRepository taxRateRepository

    HistoricalLoad(
            @Value("\${file.location}") String directoryLocation,
            ResourceLoader resourceLoader,
            DimAccountRepository dimAccountRepository,
            DimDateRepository dimDateRepository,
            DimTimeRepository dimTimeRepository,
            IndustryRepository industryRepository,
            StatusTypeRepository statusTypeRepository,
            TaxRateRepository taxRateRepository
    )
    {
        this.directoryLocation = directoryLocation
        this.resourceLoader = resourceLoader
        this.dimAccountRepository = dimAccountRepository
        this.dimDateRepository = dimDateRepository
        this.dimTimeRepository = dimTimeRepository
        this.industryRepository = industryRepository
        this.statusTypeRepository = statusTypeRepository
        this.taxRateRepository = taxRateRepository
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


    }


    private static File  getFile(List<File> files, String fileName) {
        files.find { it.name == fileName }
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


}
