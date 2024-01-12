use CoffeeShop

CREATE TABLE users (
	id INT PRIMARY KEY IDENTITY(1,1),
	username nvarchar(100) NOT NULL,
	email varchar(100) NOT NULL,
	password varchar(100) NOT NULL,
	role varchar(50) NOT NULL,
	created_at date NOT NULL,
	updated_at date NOT NULL,
	status bit NOT NULL
)

CREATE TABLE categories (
	id INT PRIMARY KEY IDENTITY(1,1),
	name varchar(100) NOT NULL,
	description text
)

CREATE TABLE products (
	product_Id INT PRIMARY KEY IDENTITY(1,1),
	product_name nvarchar(150) NOT NULL,
	product_quantity int NOT NULL,
	product_unit_price decimal(10,2) NOT NULL,
	product_banner varchar(255),
	created_at date,
	updated_at date,
	status bit,
	category_id INT,
	FOREIGN KEY (category_id) REFERENCES categories(id)
)

CREATE TABLE profile (
	profile_id INT PRIMARY KEY IDENTITY(1,1),
	first_name NVARCHAR(100),
	last_name NVARCHAR(100),
	email VARCHAR(200),
	phone VARCHAR(20),
	address NVARCHAR(50),
	dob Date,
	user_id INT UNIQUE,
	FOREIGN KEY (user_id) REFERENCES users(id)
)