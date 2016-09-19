
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
CREATE TABLE carers.claimaudit(
  transId varchar(11) NOT NULL,
  creationDate timestamp NOT NULL DEFAULT current_timestamp,
  status varchar(50) NOT NULL,
  CONSTRAINT claimauditPK PRIMARY KEY (transId, creationDate)
);
CREATE INDEX summary_value_idx ON carers.claimsummary (value);
CREATE TABLE carers.claim_status_history(
  transId varchar(11) NOT NULL,
  old_status varchar(200),
  new_status varchar(200),
  "timestamp" timestamp NOT NULL,
  CONSTRAINT claim_status_history_pkey PRIMARY KEY (transid, "timestamp")
);
CREATE OR REPLACE FUNCTION update_claim_status_history() RETURNS TRIGGER AS $BODY$
BEGIN
    IF TG_OP = 'INSERT' AND new.key = 'status' THEN
        INSERT INTO carers.claim_status_history VALUES (new.transid,null,new.value,now());
    ELSIF TG_OP = 'DELETE' AND old.key = 'status' THEN
        INSERT INTO carers.claim_status_history VALUES (old.transid,old.value,null,now());
    ELSIF TG_OP = 'UPDATE' AND new.key = 'status' THEN
        INSERT INTO carers.claim_status_history VALUES (new.transid,old.value,new.value,now());
    END IF;
    RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;
CREATE TRIGGER update_history AFTER insert or update or delete on carers.claimsummary FOR EACH ROW EXECUTE PROCEDURE update_claim_status_history();
CREATE EXTENSION tablefunc;