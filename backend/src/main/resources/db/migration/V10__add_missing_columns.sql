-- Add onboarding_state column to users table
-- This column stores user onboarding progress as a JSONB object

DO $$
BEGIN
    -- Add onboarding_state column if it doesn't exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'onboarding_state'
    ) THEN
        ALTER TABLE users ADD COLUMN onboarding_state JSONB DEFAULT '{"completedActions":[],"dismissedHelpers":[],"lastUpdated":null}'::jsonb;
        
        COMMENT ON COLUMN users.onboarding_state IS 'User onboarding progress tracking';
    END IF;
    
    -- Ensure products.variants column exists for product variant support
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'products' AND column_name = 'variants'
    ) THEN
        ALTER TABLE products ADD COLUMN variants JSONB DEFAULT '[]'::jsonb;
        
        COMMENT ON COLUMN products.variants IS 'Product variants (size, color, etc.)';
    END IF;
    
    -- Ensure sales table has fulfillment_status column
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'sales' AND column_name = 'fulfillment_status'
    ) THEN
        ALTER TABLE sales ADD COLUMN fulfillment_status TEXT NOT NULL DEFAULT 'fulfilled' 
            CHECK (fulfillment_status IN ('pending', 'fulfilled', 'shipped', 'cancelled'));
        
        COMMENT ON COLUMN sales.fulfillment_status IS 'Order fulfillment status';
    END IF;
    
    -- Ensure sales table has channel column
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'sales' AND column_name = 'channel'
    ) THEN
        ALTER TABLE sales ADD COLUMN channel TEXT NOT NULL DEFAULT 'pos' 
            CHECK (channel IN ('pos', 'online'));
        
        COMMENT ON COLUMN sales.channel IS 'Sales channel (POS or online store)';
    END IF;
END$$;

-- Create index on fulfillment_status for better query performance
CREATE INDEX IF NOT EXISTS idx_sales_fulfillment_status ON sales(fulfillment_status);

-- Create index on channel for sales analytics
CREATE INDEX IF NOT EXISTS idx_sales_channel ON sales(channel);
