CREATE TABLE Person (
    ID VARCHAR(21) NOT NULL,
    FirstName VARCHAR(255),
    LastName VARCHAR(255),
    PictureURL VARCHAR(500),
    Email VARCHAR(255),
    PRIMARY KEY (ID)
);

CREATE TABLE Student (
    StudentID VARCHAR(21) NOT NULL REFERENCES Person (ID),
    HouseName VARCHAR(30) NOT NULL,
    DormName VARCHAR(20) NOT NULL,
    RoomNumber VARCHAR(4) NOT NULL,
    PRIMARY KEY (StudentID)
);

CREATE TABLE CommunityAdviser (
    CommunityAdviserID VARCHAR(255) NOT NULL REFERENCES Person (ID),
    HouseName VARCHAR(30) NOT NULL,
    DormName VARCHAR(20) NOT NULL,
    RoomNumber VARCHAR(4) NOT NULL,
    PRIMARY KEY (CommunityAdviserID)
);

/*Stores all the residents on each floor for the CA's to have access to*/
CREATE TABLE Residents (
    HouseCAID VARCHAR(255) REFERENCES CommunityAdviser (CommunityAdviserID),
    StudentID VARCHAR(255),
    PRIMARY KEY (HouseCAID),
    FOREIGN KEY (StudentID)
        REFERENCES Student (StudentID)
);

CREATE TABLE Dorms (
    DormName VARCHAR(20),
    DormID VARCHAR(2) NOT NULL,
    PRIMARY KEY (DormID)
);

/*House primary key so items table can use HouseID as a foriegn key*/
CREATE TABLE Houses (
    HouseName VARCHAR(30),
    HouseID VARCHAR(5) NOT NULL,
    DormHouseID VARCHAR(2) NOT NULL,
    PRIMARY KEY (HouseID)
);

/*Added foreign key for connection to houses table*/
CREATE TABLE Items (
    ItemName VARCHAR(255),
    OwnerID VARCHAR(255) NOT NULL,
    ItemsHouseID VARCHAR(255),
    ItemPictureID VARCHAR(255),
    ItemID VARCHAR(255) NOT NULL,
    BorrowerID VARCHAR(255),
    DateBorrowed BIGINT,
    DateDue BIGINT,
    Available BOOLEAN,
    PRIMARY KEY (ItemID)
);

CREATE TABLE Files (
    FileID VARCHAR(70) NOT NULL,
    PRIMARY KEY (FileID)
);

CREATE TABLE UsersFiles (
    UserID VARCHAR(50) NOT NULL,
    UsersFileID VARCHAR(70),
    FOREIGN KEY (UsersFileID) REFERENCES Files(FileID)
);