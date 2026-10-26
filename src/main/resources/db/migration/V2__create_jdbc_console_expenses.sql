CREATE TABLE expense_v2 (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    expense_date DATE NOT NULL,
    category VARCHAR(20) NOT NULL CHECK (
        category IN ('FOOD', 'TRANSPORT', 'HOUSING', 'LEISURE', 'HEALTH', 'OTHER')
    ),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_expense_v2_date ON expense_v2(expense_date DESC);
