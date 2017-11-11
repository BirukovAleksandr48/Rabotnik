USE RabotnikDB

CREATE TABLE _User (
	_id int identity(1, 1) PRIMARY KEY,
	_name nvarchar(50) NOT NULL,
	_login varchar(50) NOT NULL,
	_password varchar(50) NOT NULL,
	_role varchar(20) NOT NULL
);
//////////////////////////////////////////////////////////////////
CREATE TABLE _Category (
	_id int identity(1, 1) PRIMARY KEY,
	_name nvarchar(50) NOT NULL
);
//////////////////////////////////////////////////////////////////
CREATE TABLE _Resume (
	_id int identity(1, 1) PRIMARY KEY,
	_idCreator int REFERENCES _User(_id) ON DELETE CASCADE,
	_title nvarchar(50) NOT NULL,
	_body nvarchar(500) NOT NULL,
	_city nvarchar(30) NOT NULL,
	_sallary nvarchar(20) NOT NULL,
	_idCategory int REFERENCES _Category(_id) ON DELETE CASCADE
);
//////////////////////////////////////////////////////////////////
CREATE TABLE _Vacancy (
	_id int identity(1, 1) PRIMARY KEY,
	_idCreator int REFERENCES _User(_id) ON DELETE CASCADE,
	_title nvarchar(50) NOT NULL,
	_body nvarchar(500) NOT NULL,
	_city nvarchar(30) NOT NULL,
	_sallary nvarchar(20) NOT NULL,
	_idCategory int REFERENCES _Category(_id) ON DELETE CASCADE
);
//////////////////////////////////////////////////////////////////
CREATE TABLE _FavoriteResume (
	_id int identity(1, 1) PRIMARY KEY,
	_idUser int REFERENCES _User(_id),
	_idResume int REFERENCES _Resume(_id),
);
//////////////////////////////////////////////////////////////////
CREATE TABLE _FavoriteVacancy (
	_id int identity(1, 1) PRIMARY KEY,
	_idUser int REFERENCES _User(_id) ,
	_idVacancy int REFERENCES _Vacancy(_id),
);

//////////////////////////////////////////////////////////////////
CREATE PROCEDURE signIn
	@login varchar(50),
	@password varchar(50)
AS
BEGIN
	SELECT *
	FROM _User
	WHERE _login = @login AND _password = HASHBYTES('SHA2_256', @password)
END
///////////////////////////////////////////////////////////////////
CREATE PROCEDURE getUser
	@id int
AS
BEGIN
	SELECT *
	FROM _User
	WHERE @id = _id
END
///////////////////////////////////////////////////////////////////

CREATE PROCEDURE signUp
	@login varchar(50),
	@password varchar(50),
	@name varchar(50),
	@role varchar(50)
AS
BEGIN 
	DECLARE @id int 
	SET @id = 0

	SELECT @id = _id
	FROM _User
	WHERE _login LIKE @login

	IF(@id > 0)
	BEGIN
		RETURN
	END

	INSERT INTO _User
	VALUES (@name, @login, HASHBYTES('SHA2_256', @password), @role)

	SELECT *
	FROM _User
	WHERE _id = SCOPE_IDENTITY()
END
EXECUTE signUp 'poi','g','user','user'
//////////////////////////////////////////////////////////////////
CREATE PROCEDURE AddResume
	@idCreator int,
	@title varchar(50),
	@body varchar(500),
	@city varchar(30),
	@sallary varchar(20),
	@Category varchar(30)
AS
BEGIN
	DECLARE @idCategory int
	SELECT @idCategory = _id
	FROM _Category
	WHERE _name = @Category

	INSERT INTO _Resume
	VALUES (@idCreator, @title, @body, @city, @sallary, @idCategory)
END
//////////////////////////////////////////////////////////////////
CREATE PROCEDURE SaveResume
	@id int,
	@idCreator int,
	@title varchar(50),
	@body varchar(500),
	@city varchar(30),
	@sallary varchar(20),
	@Category varchar(30)
AS
BEGIN
	DECLARE @idCategory int
	SELECT @idCategory = _id
	FROM _Category
	WHERE _name = @Category

	IF(@id = 0)
	BEGIN
		INSERT INTO _Resume
		VALUES (@idCreator, @title, @body, @city, @sallary, @idCategory)
	END
	ELSE
	BEGIN 
		UPDATE _Resume
		SET _idCategory = @idCategory, _title = @title, _body = @body, _city = @city, _sallary = @sallary, _idCreator = @idCreator
		WHERE _id = @id
	END
END
//////////////////////////////////////////////////////////////////
CREATE PROCEDURE DeleteResume
	@id int
AS
BEGIN
	DELETE FROM _Resume
	WHERE _id = @id
END
//////////////////////////////////////////////////////////////////
CREATE PROCEDURE GetResumesOfUser
	@idUser int
AS
BEGIN
	SELECT _Resume._id, _Resume._idCreator, _Resume._title, _Resume._body,
		 _Resume._city, _Resume._sallary, _Category._name
	FROM _Resume INNER JOIN _Category ON _Resume._idCategory = _Category._id
	WHERE _idCreator = @idUser
END

EXECUTE GetResumesOfUser 2
//////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////
CREATE PROCEDURE GetFavoriteResumes
	@idUser int
AS
BEGIN
	SELECT _Resume._id, _Resume._idCreator, _Resume._title, _Resume._body, _Resume._city, _Resume._sallary, _Category._name
	FROM _Resume INNER JOIN _Category ON _Resume._idCategory = _Category._id
	WHERE _idCreator IN (SELECT _id
						FROM _FavoriteResume
						WHERE _idUser = @idUser)
END
//////////////////////////////////////////////////////////////////
CREATE PROCEDURE GetCategories

AS
BEGIN
	SELECT *
	FROM _Category
END


/////////////////////////////////////////////////////////////////////
CREATE PROCEDURE FindResumes
@word varchar(50),
@city varchar(20),
@category varchar(30),
@minSalary varchar(20),
@idUser int
AS
BEGIN
	DECLARE @table table(_id int, idCreator int, _title varchar(50),
						_body varchar(500), _city varchar(30), _sallary varchar(20), _category varchar(50), isFav bit)
    
	INSERT INTO @table
    SELECT _Resume._id, _Resume._idCreator, _Resume._title, _Resume._body,
		 _Resume._city, _Resume._sallary, _Category._name, 
		 (CASE WHEN _FavoriteResume._id IS NULL THEN 0 ELSE 1 END) AS isFav
    FROM (_Resume INNER JOIN _Category ON _Resume._idCategory = _Category._id) 
		LEFT JOIN _FavoriteResume ON (_FavoriteResume._idResume = _Resume._id AND _FavoriteResume._idUser = @idUser)
    WHERE (@city IS NULL OR UPPER(_city) = UPPER(@city)) 
		AND (@category IS NULL OR _Category._name = @category) 
		AND (@minSalary IS NULL OR _sallary >= @minSalary)
		AND ((@word IS NULL) OR (_title LIKE '%'+@word+'%') OR (_body LIKE '%'+@word+'%'))

	SELECT *
	FROM @table
END

EXECUTE FindResumes 'R', NULL, NULL, NULL, 2
///////////////////
CREATE PROCEDURE FindResumes
@word varchar(50),
@city varchar(20),
@category varchar(30),
@minSalary varchar(20),
@idCreator int,
@idUser int
AS
BEGIN

    SELECT _Resume._id, _Resume._idCreator, _Resume._title, _Resume._body,
		 _Resume._city, _Resume._sallary, _Category._name, 
		 (CASE WHEN _FavoriteResume._id IS NULL THEN 0 ELSE 1 END) AS isFav
    FROM (_Resume INNER JOIN _Category ON _Resume._idCategory = _Category._id) 
		LEFT JOIN _FavoriteResume ON (_FavoriteResume._idResume = _Resume._id AND _FavoriteResume._idUser = @idUser)
    WHERE (@city IS NULL OR UPPER(_city) = UPPER(@city)) 
		AND (@category IS NULL OR _Category._name = @category) 
		AND (@minSalary IS NULL OR _sallary >= @minSalary)
		AND ((@word IS NULL) OR (_title LIKE '%'+@word+'%') OR (_body LIKE '%'+@word+'%'))
		AND ((@idCreator = 0) OR (_Resume._idCreator = @idCreator))

END
///////////////////
CREATE PROCEDURE GetFavResumes
@word varchar(50),
@idUser int
AS
BEGIN
    SELECT _Resume._id, _Resume._idCreator, _Resume._title, _Resume._body,
		 _Resume._city, _Resume._sallary, _Category._name
    FROM (_Resume INNER JOIN _Category ON _Resume._idCategory = _Category._id) 
		INNER JOIN _FavoriteResume ON (_FavoriteResume._idResume = _Resume._id AND _FavoriteResume._idUser = @idUser)
    WHERE ((@word IS NULL) OR (_title LIKE '%'+@word+'%') OR (_body LIKE '%'+@word+'%'))
END

EXECUTE GetFavResumes NULL, 2
///////////////////
CREATE PROCEDURE toggleFavRes
@idRes int,
@idUser int
AS
BEGIN
	DECLARE @idFav int
	
	SELECT @idFav = _id
	FROM _FavoriteResume
	WHERE _idResume = @idRes AND _idUser = @idUser 
	
	IF(@idFav IS NULL)
	BEGIN
		INSERT INTO _FavoriteResume
		VALUES (@idUser, @idRes)
	END
	ELSE 
	BEGIN
		DELETE FROM _FavoriteResume
		WHERE _idResume = @idRes AND _idUser = @idUser 
	END
END
EXECUTE toggleFavRes 4, 2
///////////////////////////////
CREATE PROCEDURE GetResumes
@idUser int
AS
BEGIN 
	
	SELECT _Resume._id, _Resume._idCreator, _Resume._title, _Resume._body,
		 _Resume._city, _Resume._sallary, _Category._name, 
		 (CASE WHEN _FavoriteResume._id IS NULL THEN 0 ELSE 1 END) AS isFav
    FROM (_Resume INNER JOIN _Category ON _Resume._idCategory = _Category._id) 
		LEFT JOIN _FavoriteResume ON (_FavoriteResume._idResume = _Resume._id AND _FavoriteResume._idUser = @idUser)

	
END

EXECUTE signUp 'best_coder', 'q', 'Birukov Aleksandr', 'user'
EXECUTE signUp 'worst_coder', 'q', 'Ivan Ivanych', 'user'