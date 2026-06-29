-- Create Database
CREATE DATABASE IF NOT EXISTS sparms_db;
USE sparms_db;

-- 1. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
) ENGINE=InnoDB;

-- 2. Admins Table
CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- SHA-256 Hashed
    email VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- 3. Companies Table
CREATE TABLE IF NOT EXISTS companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    website VARCHAR(255),
    industry VARCHAR(100),
    description TEXT
) ENGINE=InnoDB;

-- 4. Recruiters Table
CREATE TABLE IF NOT EXISTS recruiters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- SHA-256 Hashed
    email VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    company_id INT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Students Table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(50) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- SHA-256 Hashed
    email VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    department_id INT,
    cgpa DECIMAL(4,2) NOT NULL DEFAULT 0.00,
    tenth_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    twelfth_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    active_backlogs INT NOT NULL DEFAULT 0,
    resume_url VARCHAR(255),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 6. Skills Table
CREATE TABLE IF NOT EXISTS skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
) ENGINE=InnoDB;

-- 7. Student Skills Table (Join Table)
CREATE TABLE IF NOT EXISTS student_skills (
    student_id INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (student_id, skill_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Placement Drives Table
CREATE TABLE IF NOT EXISTS placement_drives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    job_role VARCHAR(100) NOT NULL,
    job_description TEXT,
    package_lpa DECIMAL(5,2) NOT NULL,
    min_cgpa DECIMAL(4,2) NOT NULL DEFAULT 0.00,
    max_backlogs INT NOT NULL DEFAULT 0,
    drive_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UPCOMING',
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. Applications Table
CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    drive_id INT NOT NULL,
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'APPLIED',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (drive_id) REFERENCES placement_drives(id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_drive (student_id, drive_id)
) ENGINE=InnoDB;

-- 10. Interview Rounds Table
CREATE TABLE IF NOT EXISTS interview_rounds (
    id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    round_number INT NOT NULL,
    round_name VARCHAR(100) NOT NULL,
    round_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    feedback TEXT,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    UNIQUE KEY unique_app_round (application_id, round_number)
) ENGINE=InnoDB;

-- 11. Selections Table
CREATE TABLE IF NOT EXISTS selections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    drive_id INT NOT NULL,
    application_id INT NOT NULL,
    offered_package_lpa DECIMAL(5,2) NOT NULL,
    selection_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACCEPTED',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (drive_id) REFERENCES placement_drives(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    UNIQUE KEY unique_app_selection (application_id)
) ENGINE=InnoDB;

-- 12. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_role VARCHAR(20) NOT NULL,
    user_id INT, -- NULL means broadcast to all users of that role
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;

-- Create Indexes for performance
CREATE INDEX idx_student_dept ON students(department_id);
CREATE INDEX idx_recruiter_company ON recruiters(company_id);
CREATE INDEX idx_drive_company ON placement_drives(company_id);
CREATE INDEX idx_drive_date ON placement_drives(drive_date);
CREATE INDEX idx_drive_status ON placement_drives(status);
CREATE INDEX idx_app_student ON applications(student_id);
CREATE INDEX idx_app_drive ON applications(drive_id);
CREATE INDEX idx_app_status ON applications(status);
CREATE INDEX idx_round_app ON interview_rounds(application_id);
CREATE INDEX idx_selection_student ON selections(student_id);
CREATE INDEX idx_selection_drive ON selections(drive_id);
CREATE INDEX idx_notification_user ON notifications(user_role, user_id);

-- Seed Default Departments
INSERT IGNORE INTO departments (id, name) VALUES 
(1, 'Computer Science & Engineering'),
(2, 'Information Technology'),
(3, 'Electronics & Communication Engineering'),
(4, 'Electrical & Electronics Engineering'),
(5, 'Mechanical Engineering'),
(6, 'Civil Engineering');

-- Seed Default Skills
INSERT IGNORE INTO skills (id, name) VALUES 
(1, 'Java'),
(2, 'Python'),
(3, 'C++'),
(4, 'SQL / MySQL'),
(5, 'HTML / CSS'),
(6, 'JavaScript'),
(7, 'React.js'),
(8, 'Node.js'),
(9, 'Spring Boot'),
(10, 'Data Structures & Algorithms'),
(11, 'Machine Learning'),
(12, 'Git & GitHub'),
(13, 'System Design'),
(14, 'AWS')
(15, 'MongoDB'),
(16, 'PostgreSQL'),
(17, 'Oracle Database'),
(18, 'REST APIs'),
(19, 'Microservices'),
(20, 'Docker'),
(21, 'Kubernetes'),
(22, 'Linux'),
(23, 'Operating Systems'),
(24, 'Computer Networks'),
(25, 'DBMS'),
(26, 'OOP'),
(27, 'Design Patterns'),
(28, 'Hibernate'),
(29, 'JPA'),
(30, 'Maven'),
(31, 'Gradle'),
(32, 'JUnit'),
(33, 'JUnit & Mockito'),
(34, 'Angular'),
(35, 'Vue.js'),
(36, 'Express.js'),
(37, 'TypeScript'),
(38, 'C#'),
(39, '.NET'),
(40, 'PHP'),
(41, 'Laravel'),
(42, 'Django'),
(43, 'Flask'),
(44, 'Redis'),
(45, 'Firebase'),
(46, 'Azure'),
(47, 'Google Cloud Platform'),
(48, 'DevOps'),
(49, 'CI/CD'),
(50, 'Agile Methodologies'),
(51, 'Problem Solving'),
(52, 'Communication Skills'),
(53, 'Android Development'),
(54, 'Flutter'),
(55, 'Kotlin'),
(56, 'Swift'),
(57, 'Cybersecurity'),
(58, 'Artificial Intelligence'),
(59, 'Data Analytics'),
(60, 'Power BI');

-- Seed Default Admin
-- Password hash is SHA-256 for 'admin123'
INSERT IGNORE INTO admins (id, username, password, email, name) VALUES 
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@sparms.edu', 'Placement Officer');
