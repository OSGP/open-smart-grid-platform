ALTER TABLE organisation ADD COLUMN notification_url character varying(255);

UPDATE organisation SET notification_url='https://localhost:443/web-api-smartmeter/smartMetering/notificationService/SmartMeteringNotification';