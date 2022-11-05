#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER testuser WITH PASSWORD 'testpassword';
    CREATE SCHEMA testuser;
    GRANT ALL ON SCHEMA testuser TO testuser;
    GRANT ALL ON DATABASE postgres TO testuser;
    GRANT ALL ON ALL TABLES IN SCHEMA testuser TO testuser;
EOSQL