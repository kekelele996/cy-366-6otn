INSERT INTO seats (seat_no, zone, status)
SELECT 'A01', 'A区', 'IDLE' WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'A01');
INSERT INTO seats (seat_no, zone, status)
SELECT 'A02', 'A区', 'IDLE' WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'A02');
INSERT INTO seats (seat_no, zone, status)
SELECT 'A03', 'A区', 'IDLE' WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'A03');
INSERT INTO seats (seat_no, zone, status)
SELECT 'B01', 'B区', 'IDLE' WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'B01');
INSERT INTO seats (seat_no, zone, status)
SELECT 'B02', 'B区', 'IDLE' WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'B02');
INSERT INTO seats (seat_no, zone, status)
SELECT 'B03', 'B区', 'BROKEN' WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'B03');
INSERT INTO seats (seat_no, zone, status)
SELECT 'VIP01', '包厢区', 'IDLE' WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'VIP01');
INSERT INTO seats (seat_no, zone, status)
SELECT 'VIP02', '包厢区', 'IDLE' WHERE NOT EXISTS (SELECT 1 FROM seats WHERE seat_no = 'VIP02');

INSERT INTO operation_records (module_name, owner_name, status, metric)
SELECT '机位/包厢实时状态看板', '运营组', 'ready', '100%'
WHERE NOT EXISTS (SELECT 1 FROM operation_records);
