ALTER TABLE structure ADD COLUMN completionpd smallint;
UPDATE structure set completionpd=365 WHERE extract(day from completion) = 1 and extract(month from completion) = 1;