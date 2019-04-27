fileLocation <- "utterances.csv"

# Takes a csv file and remove duplicates after stripping the unnecessary columns
prepare.watson.data.function <- function(csvData) {
  rawCsvData <- read_csv(csvData, na = "null") [, 1:3]
  cleansedData <- subset(rawCsvData, !duplicated(rawCsvData[,1]))
  thisdata <- cleansedData
  return(cleansedData)
}