const { Pool } = require('pg');
require('dotenv').config();

// Load connection string from .env
const connectionString = process.env.DATABASE_URL;

// Debug: Check if the env variable is loaded
if (!connectionString) {
  console.error("❌ DATABASE_URL is not set in .env file");
  process.exit(1);
} else {
  console.log("✅ DATABASE_URL loaded:", connectionString.split('@')[1]); 
  // only print host part for safety
}

// Create a connection pool
const pool = new Pool({
  connectionString,
  max: 20,                  // Maximum number of clients in the pool
  idleTimeoutMillis: 30000, // Idle client timeout
  connectionTimeoutMillis: 5000, // Connection attempt timeout
  ssl: {
    rejectUnauthorized: false, // Supabase requires SSL
  },
});

// Test the connection explicitly
(async () => {
  try {
    const client = await pool.connect();
    console.log("🎉 Database connected successfully to Supabase");
    client.release();
  } catch (err) {
    console.error("❌ Database connection failed:", err.message);
  }
})();

// Handle unexpected errors on idle clients
pool.on('error', (err) => {
  console.error('Unexpected error on idle client', err);
  process.exit(-1);
});

module.exports = pool;
