-- init.sql
-- To be run only when a new docker volume 'postgres-data' is created.
-- Tables must be created in this script because they will not be created by jpa until runtime.

--Create tables
CREATE TABLE category (
	id serial PRIMARY KEY,
	title VARCHAR ( 50 ) NOT NULL,
	description VARCHAR ( 200 ),
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE expected_category_distribution (
	id serial PRIMARY KEY,
	category_id INT NOT NULL,
	minimum_distribution int NOT NULL,
	maximum_distribution int NOT NULL,
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	FOREIGN KEY (category_id)
    	REFERENCES category (id)
);

CREATE TABLE expense (
	id serial PRIMARY KEY,
	category_id INT NOT NULL,
	title VARCHAR ( 50 ) NOT NULL,
	description VARCHAR ( 200 ),
	created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	FOREIGN KEY (category_id)
    	REFERENCES category (id)
);

--Insert default category and expected_category_distribution data
INSERT INTO category (title, description, created_date, last_updated_date)
VALUES
    ('Housing', 'Expenses related to housing.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),
    ('Transportation', 'Costs associated with transportation.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),
    ('Food', 'Expenditures on food.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),
    ('Utilities', 'Costs for utilities.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),
    ('Insurance', 'Expenditures for various types of insurance coverage.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),
    ('Medical & Healthcare', 'Expenses related to medical and healthcare services.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),
    ('Saving, Investing, & Debt Payments', 'Allocations for saving, investing, and debt payments.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),
    ('Personal Spending', 'Personal discretionary spending.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),
    ('Recreation & Entertainment', 'Costs associated with recreation and entertainment.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),
    ('Miscellaneous', 'Miscellaneous expenses.', '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258');

INSERT INTO expected_category_distribution (category_id, minimum_distribution, maximum_distribution, created_date, last_updated_date)
VALUES
    (1, 25, 35, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),  -- Housing
    (2, 10, 15, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),  -- Transportation
    (3, 10, 15, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),  -- Food
    (4, 5, 10, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),   -- Utilities
    (5, 10, 25, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),  -- Insurance
    (6, 5, 10, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),   -- Medical & Healthcare
    (7, 10, 20, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),  -- Saving, Investing, & Debt Payments
    (8, 5, 10, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),   -- Personal Spending
    (9, 5, 10, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258'),   -- Recreation & Entertainment
    (10, 5, 10, '2023-11-30 13:56:02.845258', '2023-11-30 13:56:02.845258');  -- Miscellaneous