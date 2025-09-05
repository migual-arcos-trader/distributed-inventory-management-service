MERGE INTO inventory_items (id, product_id, store_id, current_stock, reserved_stock, minimum_stock_level, maximum_stock_level, last_updated, version, created_at, updated_at)
KEY(id)
VALUES
('test-item-1', 'test-prod-1', 'test-store-1', 100, 10, 5, 200, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO inventory_items (id, product_id, store_id, current_stock, reserved_stock, minimum_stock_level, maximum_stock_level, last_updated, version, created_at, updated_at)
KEY(id)
VALUES
('test-item-2', 'test-prod-2', 'test-store-1', 50, 5, 2, 100, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO inventory_items (id, product_id, store_id, current_stock, reserved_stock, minimum_stock_level, maximum_stock_level, last_updated, version, created_at, updated_at)
KEY(id)
VALUES
('test-item-3', 'test-prod-1', 'test-store-2', 200, 20, 10, 500, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO inventory_items (id, product_id, store_id, current_stock, reserved_stock, minimum_stock_level, maximum_stock_level, last_updated, version, created_at, updated_at)
KEY(id)
VALUES
('test-item-4', 'test-prod-3', 'test-store-2', 75, 15, 5, 150, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);