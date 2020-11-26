CREATE TABLE IF NOT EXISTS t_item (
    item_id SERIAL PRIMARY KEY,
    guid UUID DEFAULT uuid(),
    name VARCHAR(64) NOT NULL,
    description VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS t_lookup_value (
    lkval_id SERIAL PRIMARY KEY,
    entity_type VARCHAR(64) NOT NULL,
    entity_id NUMBER,
    lookup_value_type VARCHAR(64) NOT NULL,
    lookup_value VARCHAR(64) NOT NULL
);
