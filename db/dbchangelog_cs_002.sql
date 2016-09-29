--liquibase formatted sql

--changeset jmigueis:cs_002_1
CREATE INDEX summary_value_idx ON carers.claimsummary (value);
--rollback DROP INDEX IF EXISTS carers.summary_value_idx;

--changeset jmigueis:cs_002_2
ALTER TABLE carers.claim ALTER COLUMN transid TYPE CHARACTER VARYING(11);
--rollback ALTER TABLE carers.claim ALTER COLUMN transid TYPE CHARACTER VARYING(7);

--changeset jmigueis:cs_002_3
ALTER TABLE carers.claimaudit ALTER COLUMN transid TYPE CHARACTER VARYING(11);
--rollback ALTER TABLE carers.claimaudit ALTER COLUMN transid TYPE CHARACTER VARYING(7);

--changeset jmigueis:cs_002_4
ALTER TABLE carers.claimsummary ALTER COLUMN transid TYPE CHARACTER VARYING(11);
--rollback ALTER TABLE carers.claimsummary ALTER COLUMN transid TYPE CHARACTER VARYING(7);
