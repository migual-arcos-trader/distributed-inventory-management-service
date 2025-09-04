CREATE TABLE IF NOT EXISTS inventory_items (
    id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    store_id VARCHAR(255) NOT NULL,
    current_stock INT NOT NULL DEFAULT 0,
    reserved_stock INT NOT NULL DEFAULT 0,
    minimum_stock_level INT,
    maximum_stock_level INT,
    last_updated TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_product_store UNIQUE (product_id, store_id)
);

CREATE INDEX IF NOT EXISTS idx_inventory_store ON inventory_items(store_id);
CREATE INDEX IF NOT EXISTS idx_inventory_product ON inventory_items(product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_version ON inventory_items(version);