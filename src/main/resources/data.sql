-- Add Top Level Manager To DB
INSERT INTO staff_members (
    staff_id,
    email,
    employment_status,
    first_name,
    surname,
    department,
    hire_date,
    line_manager_id,
    job_role,
    employment_type,
    job_level,
    role_start_date
) VALUES (
             '11111111-1111-1111-1111-111111111111',
             'supmanager@email.com',
             'ACTIVE',
             'Manager',
             'Supreme',
             'MANAGEMENT',
             '2005-01-01',
             '11111111-1111-1111-1111-111111111111',
             'Manager Supreme',
             'FULL_TIME',
             'SUPREME',
             '2022-01-01'
         );

-- Manager Supreme's Leave Allowance
INSERT INTO leave_allowances (
    id,
    staff_id,
    first_name,
    surname,
    manager_id,
    allowance_year,
    total_allowance,
    used_allowance
) VALUES (
             '2181d3ce-5b76-40cc-92d2-e7edf1fd59d7', -- Generated ID for the allowance record
             '11111111-1111-1111-1111-111111111111', -- Matches Manager's staff_id
             'Manager',
             'Supreme',
             '11111111-1111-1111-1111-111111111111',
             2026,
             25.0,
             0.0
         );

-- Add a basic staff member (Charlie Smitty)
INSERT INTO staff_members (
    staff_id,
    email,
    employment_status,
    first_name,
    surname,
    department,
    hire_date,
    line_manager_id,
    job_role,
    employment_type,
    job_level,
    role_start_date
) VALUES (
             '24dc72c4-58f1-4921-bf61-0190d1401fee',
             'staff.member@email.com',
             'ACTIVE',
             'Charlie',
             'Smitty',
             'Testing',
             '2026-09-18',
             '11111111-1111-1111-1111-111111111111',
             'Tester',
             'FULL_TIME',
             'JUNIOR',
             '2026-09-18'
         );

-- Add Staff Member (Charlie Smitty) a Leave Allowance
INSERT INTO leave_allowances (
    id,
    staff_id,
    first_name,
    surname,
    manager_id,
    allowance_year,
    total_allowance,
    used_allowance
) VALUES (
             '01a0b4f6-e0ef-7e09-81f5-2b6bed278b75',
             '24dc72c4-58f1-4921-bf61-0190d1401fee',
             'Charlie',
             'Smitty',
             '11111111-1111-1111-1111-111111111111',
             2026,
             25.0,
             0.0
         );