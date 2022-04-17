insert into Person(FirstName, LastName, PictureURL, ID, Email)
values ('Alex', 'Howe', NULL, 123456789, 'alex.howe@gmail.com');

insert into Person(FirstName, LastName, PictureURL, ID, Email)
values ('Logan', 'Marston', NULL, 987654321, 'logan.marston@gmail.com');

insert into Person(FirstName, LastName, PictureURL, ID, Email)
values ('Dave', 'Hoover', NULL, 123459876, 'dave.hoover@gmail.com');

insert into Student(StudentID, HouseName, DormName, RoomNumber)
values (123456789, 'Converse', 'Friley', '3462');

insert into CommunityAdviser(CommunityAdviserID, HouseName, DormName, RoomNumber)
values (987654321, 'Converse', 'Friley', '3482');

insert into Vendor(VendorID, CompanyName)
values (123459876, 'Xbox');

insert into Residents(HouseCAID, StudentID)
values(987654321, 123456789);

insert into ChatUsers(ChatID, OwnerID, BorrowerID)
values('01', 123456789, 987654321);

insert into ChatMessages(ChatID, MessageID, MessageText, timeOfMessage)
values ('01', '0001', 'Hello!', current_timestamp());

insert into Items(ItemName, OwnerID, ItemsHouseID, ItemPictureID, ItemID, BorrowerID, DateBorrowed, DateDue, Available)
values('Xbox 360', 123456789, '21', null, '33', null, null, null, TRUE);

insert into Items(ItemName, OwnerID, ItemsHouseID, ItemPictureID, ItemID, BorrowerID, DateBorrowed, DateDue, Available)
values('Speakers', 987654321, '21', null, '34', null, null, null, TRUE);

insert into Items(ItemName, OwnerID, ItemsHouseID, ItemPictureID, ItemID, BorrowerID, DateBorrowed, DateDue, Available)
values('Long Board', 123459876, '21', null, '35', 987654321, 0, 0, FALSE);

insert into Items(ItemName, OwnerID, ItemsHouseID, ItemPictureID, ItemID, BorrowerID, DateBorrowed, DateDue, Available)
values('Long Board 2', 123459876, '21', null, '36', null, null, null, TRUE);

insert into Files(FileID)
values('1');

insert into Files(FileID)
values('2');

insert into Files(FileID)
values('3');

insert into UsersFiles(UserID, UsersFileID)
values(123459876, '1');

insert into UsersFiles(UserID, UsersFileID)
values(123459876, '2');

insert into UsersFiles(UserID, UsersFileID)
values(456789123, '2');

insert into UsersFiles(UserID, UsersFileID)
values(123459876, '3');

insert into UsersFiles(UserID, UsersFileID)
values(987654321, '3');