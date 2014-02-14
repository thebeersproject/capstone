CREATE TABLE IF NOT EXISTS `capstone`.`index` (
    `Agent Name` VARCHAR(50) NOT NULL,
    `Instance` VARCHAR(50) NOT NULL,
    `Service Name` VARCHAR(50) NOT NULL,
    `Index` BIGINT NOT NULL,
    PRIMARY KEY (`Agent Name` , `Instance` , `Service Name`),
    UNIQUE INDEX `Index_UNIQUE` (`Index` ASC)
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`basedata` (
    `Index` BIGINT NOT NULL,
    `Year` INT NOT NULL,
    `Month` INT NOT NULL,
    `Day` INT NOT NULL,
    `Hour` INT NOT NULL,
    `Minute` INT NOT NULL,
    `Second` INT NOT NULL,
    `StartupTime` VARCHAR(50) NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`Index` , `Year` , `Month` , `Day` , `Hour` , `Minute` , `Second`),
    CONSTRAINT `BDIndex` FOREIGN KEY (`Index`)
        REFERENCES `capstone`.`index` (`Index`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`6mindata` (
    `Index` BIGINT NOT NULL,
    `Year` INT NOT NULL,
    `Month` INT NOT NULL,
    `Day` INT NOT NULL,
    `Hour` INT NOT NULL,
    `Interval` INT NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`Index` , `Year` , `Month` , `Day` , `Hour` , `Interval`),
    CONSTRAINT `6MinIndex` FOREIGN KEY (`Index`)
        REFERENCES `capstone`.`index` (`Index`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`hourdata` (
    `Index` BIGINT NOT NULL,
    `Year` INT NOT NULL,
    `Month` INT NOT NULL,
    `Day` INT NOT NULL,
    `Hour` INT NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`Index` , `Year` , `Month` , `Day` , `Hour`),
    CONSTRAINT `HourIndex` FOREIGN KEY (`Index`)
        REFERENCES `capstone`.`index` (`Index`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`daydata` (
    `Index` BIGINT NOT NULL,
    `Year` INT NOT NULL,
    `Month` INT NOT NULL,
    `Day` INT NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`Index` , `Year` , `Month` , `Day`),
    CONSTRAINT `DayIndex` FOREIGN KEY (`Index`)
        REFERENCES `capstone`.`index` (`Index`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `capstone`.`totaldata` (
    `Index` BIGINT NOT NULL,
    `Service Calls` BIGINT NOT NULL,
    `Service Time` BIGINT NOT NULL,
    PRIMARY KEY (`Index`),
    CONSTRAINT `TDIndex` FOREIGN KEY (`Index`)
        REFERENCES `capstone`.`index` (`Index`)
        ON DELETE RESTRICT ON UPDATE CASCADE
)  ENGINE=InnoDB;
