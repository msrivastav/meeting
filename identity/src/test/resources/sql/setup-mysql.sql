CREATE TABLE `organisation`
(
    `organisation_id`   int          NOT NULL AUTO_INCREMENT,
    `organisation_name` varchar(200) NOT NULL,
    `email_domain`      varchar(100) NOT NULL,
    PRIMARY KEY (`organisation_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1702
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO `organisation`
(`organisation_id`,
 `organisation_name`,
 `email_domain`)
VALUES (1701,
        'Star Trek',
        'gmail.com');


CREATE TABLE `external_contact`
(
    `external_contact_id` int          NOT NULL AUTO_INCREMENT,
    `email_id_prefix`     varchar(100) NOT NULL,
    `contact_org_id`      int          NOT NULL,
    PRIMARY KEY (`external_contact_id`),
    KEY `organisation_foreign_key_idx` (`contact_org_id`),
    CONSTRAINT `organisation_foreign_key` FOREIGN KEY (`contact_org_id`) REFERENCES `organisation` (`organisation_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 18101988
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO `external_contact`
(`external_contact_id`,
 `email_id_prefix`,
 `contact_org_id`)
VALUES (18101987,
        'manoo.srivastav',
        1701);


