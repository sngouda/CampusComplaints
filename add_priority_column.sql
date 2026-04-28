-- ============================================================
-- CampusCare Priority Feature - Database Migration
-- Run this script ONCE in your MySQL client
-- ============================================================

-- Step 1: Add the priority column to the complaints table
ALTER TABLE complaints
    ADD COLUMN priority ENUM('High', 'Medium', 'Low') NOT NULL DEFAULT 'Medium'
    AFTER description;

-- Step 2: Verify the change
DESCRIBE complaints;

-- Step 3 (Optional): Update any existing complaints to Medium priority (already the default)
UPDATE complaints SET priority = 'Medium' WHERE priority IS NULL;

-- Done! Your complaints table now supports High / Medium / Low priority.
