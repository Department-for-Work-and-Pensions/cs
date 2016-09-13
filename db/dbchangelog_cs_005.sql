--liquibase formatted sql

--changeset pwhitehead:cs_005_1
ALTER TABLE carers.claim ADD CONSTRAINT claim_checkorigin CHECK (originTag in ('GB', 'GB-NIR'));
--rollback ALTER TABLE carers.claim DROP CONSTRAINT IF EXISTS claim_checkorigin;

--changeset pwhitehead:cs_005_2
ALTER TABLE carers.claimsummary ADD CONSTRAINT claimsummary_checkorigin CHECK (originTag in ('GB', 'GB-NIR'));
--rollback ALTER TABLE carers.claimsummary DROP CONSTRAINT IF EXISTS claimsummary_checkorigin;
