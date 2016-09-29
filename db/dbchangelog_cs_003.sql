--liquibase formatted sql

--changeset jmigueis:cs_003_1
ALTER TABLE carers.claim ADD COLUMN drs_status numeric default 1 not null;
--rollback ALTER TABLE carers.claim DROP COLUMN IF EXISTS drs_status;
