CREATE TABLE IF NOT EXISTS `capstone`.`indextable` (
    `Agent Name` VARCHAR(50) NOT NULL,
    `Instance` VARCHAR(50) NOT NULL,
    `Service Name` VARCHAR(50) NOT NULL,
    `IndexColumn` BIGINT NOT NULL,
    PRIMARY KEY (`Agent Name` , `Instance` , `Service Name`),
    UNIQUE INDEX `Index_UNIQUE` (`IndexColumn` ASC)
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`basedata` (
    `IndexColumn` BIGINT NOT NULL,
    `Year` INT NOT NULL,
    `Month` INT NOT NULL,
    `Day` INT NOT NULL,
    `Hour` INT NOT NULL,
    `Minute` INT NOT NULL,
    `Second` INT NOT NULL,
    `StartupTime` VARCHAR(50) NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`IndexColumn` , `Year` , `Month` , `Day` , `Hour` , `Minute` , `Second`),
    CONSTRAINT `BDIndex` FOREIGN KEY (`IndexColumn`)
        REFERENCES `capstone`.`indextable` (`IndexColumn`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`6mindata` (
    `IndexColumn` BIGINT NOT NULL,
    `Year` INT NOT NULL,
    `Month` INT NOT NULL,
    `Day` INT NOT NULL,
    `Hour` INT NOT NULL,
    `Interval` INT NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`IndexColumn` , `Year` , `Month` , `Day` , `Hour` , `Interval`),
    CONSTRAINT `6MinIndex` FOREIGN KEY (`IndexColumn`)
        REFERENCES `capstone`.`indextable` (`IndexColumn`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`hourdata` (
    `IndexColumn` BIGINT NOT NULL,
    `Year` INT NOT NULL,
    `Month` INT NOT NULL,
    `Day` INT NOT NULL,
    `Hour` INT NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`IndexColumn` , `Year` , `Month` , `Day` , `Hour`),
    CONSTRAINT `HourIndex` FOREIGN KEY (`IndexColumn`)
        REFERENCES `capstone`.`indextable` (`IndexColumn`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`daydata` (
    `IndexColumn` BIGINT NOT NULL,
    `Year` INT NOT NULL,
    `Month` INT NOT NULL,
    `Day` INT NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`IndexColumn` , `Year` , `Month` , `Day`),
    CONSTRAINT `DayIndex` FOREIGN KEY (`IndexColumn`)
        REFERENCES `capstone`.`indextable` (`IndexColumn`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`totaldata` (
    `IndexColumn` BIGINT NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`IndexColumn`),
    CONSTRAINT `TDIndex` FOREIGN KEY (`IndexColumn`)
        REFERENCES `capstone`.`indextable` (`IndexColumn`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;