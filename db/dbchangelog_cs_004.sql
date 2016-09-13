--liquibase formatted sql

--changeset pwhitehead:cs_004_1
ALTER TABLE carers.claim ADD COLUMN originTag varchar(6) default 'GB' not null;
--rollback ALTER TABLE carers.claim DROP COLUMN IF EXISTS originTag;

--changeset pwhitehead:cs_004_2
ALTER TABLE carers.claimsummary ADD COLUMN originTag varchar(6) default 'GB' not null;
--rollback ALTER TABLE carers.claimsummary DROP COLUMN IF EXISTS originTag;
