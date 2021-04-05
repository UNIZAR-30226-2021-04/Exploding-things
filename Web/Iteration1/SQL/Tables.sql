DROP TABLE if exists Mazo;
DROP TABLE if exists Mano;
DROP TABLE if exists Users;
DROP TABLE if exists Lobby;
DROP TABLE if exists Cartas;

CREATE TABLE Lobby(
	id_lobby integer PRIMARY KEY identity(1,1),
	nusers integer default 1,
	pstart integer default 0,
);
CREATE TABLE Users(
	Username varchar(255) PRIMARY KEY,
	Password varchar(255) NOT NULL,
	id_lobby int,
	FOREIGN KEY (id_lobby) REFERENCES Lobby(id_lobby)
);
CREATE TABLE Cartas(
	id_carta varchar(255) PRIMARY KEY
);
CREATE TABLE Mano(
	Username varchar(255),
	id_carta varchar(255),
	PRIMARY KEY(Username,id_carta),
	num integer NOT NULL,
	FOREIGN KEY (Username) REFERENCES Users(Username),
	FOREIGN KEY (id_carta) REFERENCES Cartas(id_carta)	
);
CREATE TABLE Mazo(
	id_lobby integer,
	id_carta varchar(255),
	PRIMARY KEY(id_lobby,id_carta),
	num integer NOT NULL,
	FOREIGN KEY (id_lobby) REFERENCES Lobby(id_lobby),
	FOREIGN KEY (id_carta) REFERENCES Cartas(id_carta)
);


