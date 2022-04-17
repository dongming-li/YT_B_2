/* selects the first and last name of all students in DB*/
SELECT 
    p.FirstName, p.LastName
FROM
    Student s,
    Person p
WHERE
    p.ID = s.StudentID;

/*selects the first and last name and house of all CA's in the DB*/
SELECT 
    p.FirstName, p.LastName, c.HouseName
FROM
    CommunityAdviser c,
    Person p
WHERE
    p.ID = c.CommunityAdviserID;
    
/*selects the first and last name of all vendors in the DB*/
SELECT 
    p.FirstName, p.LastName
FROM
    Person p,
    Vendor v
WHERE
    p.ID = v.VendorID;
    
/*Students items connection*/
SELECT 
    p.FirstName, i.ItemName, i.Available
FROM
    Person p,
    Items i
WHERE
    p.ID = i.OwnerID;
    
/*ChatUser testing*/
SELECT 
    p1.FirstName, p2.FirstName
FROM
    Person p1,
    Person p2,
    ChatUsers c2
WHERE
    p1.ID = c2.OwnerID
        AND p2.ID = c2.BorrowerID;

/*ChatMessage testing*/
SELECT 
    c2.MessageText
FROM
    Person p1,
    Person p2,
    ChatUsers c1,
    ChatMessages c2
WHERE
    p1.ID = c1.OwnerID
        AND p2.ID = c1.BorrowerID
        AND c1.ChatID = c2.ChatID;

/*House and Dorm connection*/
SELECT 
    d.DormName, h.HouseName
FROM
    Houses h,
    Dorms d
WHERE
    h.DormHouseID = d.DormID;


/*CA and resident connection*/

SELECT 
    cm.timeOfMessage
FROM
    ChatMessages cm,
    ChatUsers cu
WHERE
    cu.ChatID = cm.ChatID;
    
/*One users items*/
SELECT 
    i.ItemName
FROM
    Items i
WHERE
    i.OwnerID = '123459876';
    
SELECT 
    p.FirstName
FROM
    Person p,
    Items i
WHERE
    p.ID = i.OwnerID;