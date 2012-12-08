source dls_down.sql;

CREATE TABLE administration
(
  adminId varchar(25) NOT NULL,
  adminCredentials VARCHAR(40) NOT NULL,

  PRIMARY KEY (adminId)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/**
 *  playStatus: 
 *    0 - Open, (choices still be selected)
 *    1 - Closed, (choices complete and edge playing has begun)
 *    2 - Finished (edge playing is done)
 */
CREATE TABLE event
(
  id VARCHAR(255) NOT NULL,
  question VARCHAR(255) NOT NULL DEFAULT ' ',
  numberOfChoices INT,
  numberOfEdges INT,
  isAsynchronous BIT NOT NULL DEFAULT 0,
  moderator VARCHAR(25),
  playStatus INT NOT NULL DEFAULT 1,
  createdDate datetime,
  
  PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=latin1;

CREATE TABLE user
(
  userName VARCHAR(25) NOT NULL,
  userPassword VARCHAR(25) NOT NULL DEFAULT ' ',
  eventId VARCHAR(255) NOT NULL,
  position INT NOT NULL,

  PRIMARY KEY (userName, eventId),
  FOREIGN KEY (eventId) REFERENCES event(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=latin1;

CREATE TABLE choice
(
  orderValue INT NOT NULL,
  eventId VARCHAR(255) NOT NULL, 
  name VARCHAR(255) NOT NULL,
  finalDecisionOrder INT NOT NULL DEFAULT -1,  

  PRIMARY KEY (orderValue, EventId),
  FOREIGN KEY (EventId) REFERENCES event(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=latin1;

CREATE TABLE edge
(
  height INT NOT NULL,
  choiceId INT NOT NULL,
  eventId VARCHAR(255) NOT NULL,

  PRIMARY KEY (height, choiceId),
  FOREIGN KEY (eventId) REFERENCES event(id) ON DELETE CASCADE, 
  FOREIGN KEY (eventId, choiceId) REFERENCES choice(eventId, orderValue) 
ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=latin1;


/* change the standard delimiter */
DELIMITER $$

CREATE PROCEDURE procUpdateEvent (idIN VARCHAR(255), questionIN 
VARCHAR(255), numberOfChoicesIN INT, numberOfEdgesIN INT, isAsynchronousIN 
BIT, moderatorIN VARCHAR(25), playStatusIN INT, createdDateSIN 
VARCHAR(255))
BEGIN
IF NOT EXISTS (SELECT 1 FROM event WHERE id=idIN) THEN
  INSERT INTO event (id, question, numberOfChoices, numberOfEdges, 
isAsynchronous, moderator, playStatus, createdDate)
  VALUES (idIN, questionIN, numberOfChoicesIN, numberOfEdgesIN, 
isAsynchronousIN, moderatorIN, playStatusIN, str_to_date(createdDateSIN, 
'%Y-%m-%d'));
ELSE
  UPDATE event
  SET question=questionIN, numberOfChoices=numberOfChoicesIN, 
numberOfEdges=numberOfEdgesIN, isAsynchronous=isAsynchronousIN, 
moderator=moderatorIN, playStatus=playStatusIN, 
createdDate=str_to_date(createdDateSIN, '%Y-%m-%d')
  WHERE id=idIN;
END IF;
END$$

CREATE PROCEDURE procUpdateEdge (EventIdIN VARCHAR(255), heightIN INT, 
choiceIdIN INT)
BEGIN
IF NOT EXISTS (SELECT 1 FROM edge WHERE height=heightIN and 
choiceId=choiceIdIN) THEN
  INSERT INTO edge (EventId, height, choiceId) 
  VALUES (EventIdIN, heightIN, choiceIdIN);
ELSE 
  UPDATE edge
  SET EventId = EventIdIN
  WHERE height = heightIN AND choiceId = choiceIdIN;
END IF;
END$$

CREATE PROCEDURE procUpdateUser (eventIdIN VARCHAR(255), userNameIN 
VARCHAR(25), userPasswordIn VARCHAR(25), positionIN INT)
BEGIN
IF NOT EXISTS (SELECT 1 FROM user WHERE eventId=eventIdIN AND 
userName=userNameIN) THEN
  INSERT INTO user (userName, userPassword, eventId, position)
  VALUES (userNameIN, userPasswordIN, eventIdIN, positionIN);
ELSE
  UPDATE user
  SET userPassword=userPasswordIN, position=positionIN
  WHERE eventId=eventIdIN and userName=userNameIN;
END IF;
END$$

CREATE PROCEDURE procUpdateChoice (eventIdIN VARCHAR(255), orderValueIN 
INT, nameIN varchar(255), finalDecisionOrderIN INT)
BEGIN
IF NOT EXISTS (SELECT 1 FROM choice WHERE eventId=eventIdIN and 
orderValue=orderValueIN) THEN
  INSERT INTO choice (eventId, orderValue, name, finalDecisionOrder)
  VALUES (eventIdIN, orderValueIN, nameIN, finalDecisionOrderIN);
ELSE
  UPDATE choice
  SET name=nameIN, finalDecisionOrder=finalDecisionOrderIN
  WHERE eventId=eventIdIN AND orderValue=orderValueIN;
END IF;
END$$


/* revert the standard delimiter after all procedures are created */
DELIMITER ;

/* initialization of standard variables */
INSERT INTO administration (adminId, adminCredentials)
VALUES ('andrew', MD5('andrew'));

