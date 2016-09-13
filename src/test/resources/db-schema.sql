DROP ALL OBJECTS;
CREATE SCHEMA if not exists carers;
CREATE TABLE carers.claim(
  transId varchar(11) NOT NULL PRIMARY KEY,
  claimXml text NOT NULL,
  drs_status numeric DEFAULT 1 NOT NULL,
  originTag varchar(6) DEFAULT 'GB' NOT NULL,
  CONSTRAINT claim_checkorigin CHECK (originTag in ('GB', 'GB-NIR'))
);
CREATE TABLE carers.claimsummary(
  transId varchar(11) NOT NULL,
  key varchar(20) NOT NULL,
  value varchar(200) NOT NULL,
  originTag varchar(6) DEFAULT 'GB' NOT NULL,
  CONSTRAINT claimsummaryPK PRIMARY KEY (transId, key),
  CONSTRAINT claimsummary_checkorigin CHECK (originTag in ('GB', 'GB-NIR'))
);
create table carers.claimaudit(
  transId varchar(11) NOT NULL,
  creationDate timestamp NOT NULL DEFAULT current_timestamp,
  status varchar(50) NOT NULL,
  CONSTRAINT claimauditPK PRIMARY KEY (transId, creationDate)
);
CREATE INDEX summary_value_idx ON carers.claimsummary (value);