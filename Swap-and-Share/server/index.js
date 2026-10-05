const express = require('express');
const cors = require('cors');
const passport = require('passport');
const GoogleStrategy = require('passport-google-oauth20').Strategy;
const session = require('express-session');
const pool = require('./db');
require('dotenv').config();

const app = express();

// Middleware
app.use(cors({
    origin: "http://localhost:5173",
    credentials: true
}));
app.use(express.json());
app.use(session({
    secret: process.env.SESSION_SECRET,
    resave: false,
    saveUninitialized: false
}));
app.use(passport.initialize());
app.use(passport.session());

// Passport configuration
passport.use(new GoogleStrategy({
    clientID: process.env.GOOGLE_CLIENT_ID,
    clientSecret: process.env.GOOGLE_CLIENT_SECRET,
    callbackURL: "http://localhost:5000/auth/google/callback"
},
async function(accessToken, refreshToken, profile, done) {
    try {
        console.log("Google profile:", profile); // Debug log

        // Check if user exists
        const currentUser = await pool.query(
            "SELECT * FROM users WHERE google_id = $1",
            [profile.id]
        );

        if (currentUser.rows.length > 0) {
            // Update existing user's profile picture
            const updatedUser = await pool.query(
                "UPDATE users SET profile_picture = $1 WHERE google_id = $2 RETURNING *",
                [profile.photos[0].value, profile.id]
            );
            console.log("Updated user:", updatedUser.rows[0]); // Debug log
            done(null, updatedUser.rows[0]);
        } else {
            // Create new user
            const newUser = await pool.query(
                "INSERT INTO users (google_id, username, email, profile_picture) VALUES ($1, $2, $3, $4) RETURNING *",
                [
                    profile.id,
                    profile.displayName,
                    profile.emails[0].value,
                    profile.photos[0].value
                ]
            );
            console.log("New user created:", newUser.rows[0]); // Debug log
            done(null, newUser.rows[0]);
        }
    } catch (err) {
        console.error("Error in Google Strategy:", err);
        done(err, null);
    }
}));

passport.serializeUser((user, done) => {
    done(null, user.user_id);
});

passport.deserializeUser(async (id, done) => {
    try {
        const user = await pool.query(
            "SELECT * FROM users WHERE user_id = $1",
            [id]
        );
        done(null, user.rows[0]);
    } catch (err) {
        done(err, null);
    }
});

// Auth Routes
app.get('/auth/google',
    passport.authenticate('google', { scope: ['profile', 'email'] })
);

app.get('/auth/google/callback', 
    passport.authenticate('google', { 
        failureRedirect: 'http://localhost:5173/login',
        successRedirect: 'http://localhost:5173'
    })
);

app.post('/auth/logout', (req, res) => {
    req.logout(function (err) {
        if (err) {
            return res.status(500).json({ error: err.message });
        }

        req.session.destroy(() => {
            res.clearCookie('connect.sid');
            res.status(200).json({ message: "Logged out successfully" });
        });
    });
});


// Check if user is authenticated
app.get('/auth/user', (req, res) => {
    if (req.user) {
        console.log("Current user:", req.user); // Debug log
        res.json(req.user);
    } else {
        res.status(401).json({ error: "Not authenticated" });
    }
});

//start
app.post('/auth/test-login', async (req, res) => {
    try {
        const { email, password } = req.body;

        const TEST_EMAIL = "selenium.test@gmail.com";
        const TEST_PASSWORD = "SeleniumTest@123";

        if (email !== TEST_EMAIL || password !== TEST_PASSWORD) {
            return res.status(401).json({
                error: "Invalid test credentials"
            });
        }

        // Get an existing user from the database
        const result = await pool.query(
            "SELECT * FROM users LIMIT 1"
        );

        if (result.rows.length === 0) {
            return res.status(500).json({
                error: "No user found in database"
            });
        }

        const testUser = result.rows[0];

        req.login(testUser, (err) => {
            if (err) {
                console.error("Test login error:", err);

                return res.status(500).json({
                    error: "Could not create test session"
                });
            }

            return res.status(200).json({
                message: "Selenium test login successful"
            });
        });

    } catch (err) {
        console.error("Test login error:", err);

        return res.status(500).json({
            error: "Test login failed"
        });
    }
});
// END

// Debug route to check profile picture
app.get('/auth/check-profile', async (req, res) => {
    if (!req.user) {
        return res.status(401).json({ error: "Not authenticated" });
    }
    try {
        const user = await pool.query(
            "SELECT * FROM users WHERE user_id = $1",
            [req.user.user_id]
        );
        res.json({
            user: user.rows[0],
            session: req.user,
            profile_picture: user.rows[0].profile_picture
        });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Protected route middleware
const isAuthenticated = (req, res, next) => {
    if (req.isAuthenticated()) {
        return next();
    }
    res.status(401).json({ error: "Please log in first" });
};

// Product Routes
app.get('/api/products', async (req, res) => {
    try {
        const allProducts = await pool.query("SELECT * FROM products");
        res.json(allProducts.rows);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Protected Cart Routes
app.get('/api/cart', isAuthenticated, async (req, res) => {
    try {
        const cart = await pool.query(
            "SELECT c.cart_id, c.quantity, p.* FROM cart c JOIN products p ON c.product_id = p.product_id WHERE c.user_id = $1",
            [req.user.user_id]
        );
        res.json(cart.rows);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});

// Product listing routes
app.post('/api/products/sell', isAuthenticated, async (req, res) => {
    try {
        const {
            name,
            description,
            price,
            size,
            condition,
            brand,
            original_price,
            category,
            gender,
            age_group,
            image_url
        } = req.body;

        const newProduct = await pool.query(
            `INSERT INTO products (
                name, description, price, size, condition, 
                brand, original_price, category, gender, 
                age_group, image_url, seller_id
            ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12) 
            RETURNING *`,
            [
                name, description, price, size, condition,
                brand, original_price, category, gender,
                age_group, image_url, req.user.user_id
            ]
        );

        res.json(newProduct.rows[0]);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Get user's listings
app.get('/api/products/my-listings', isAuthenticated, async (req, res) => {
    try {
        const myListings = await pool.query(
            "SELECT * FROM products WHERE seller_id = $1",
            [req.user.user_id]
        );
        res.json(myListings.rows);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Rating routes
app.post('/api/ratings', isAuthenticated, async (req, res) => {
    try {
        const { user_id, rating, comment } = req.body;

        // Check if user has already rated this seller
        const existingRating = await pool.query(
            "SELECT * FROM ratings WHERE user_id = $1 AND rated_by = $2",
            [user_id, req.user.user_id]
        );

        if (existingRating.rows.length > 0) {
            return res.status(400).json("You have already rated this user");
        }

        // Add new rating
        const newRating = await pool.query(
            "INSERT INTO ratings (user_id, rated_by, rating, comment) VALUES ($1, $2, $3, $4) RETURNING *",
            [user_id, req.user.user_id, rating, comment]
        );

        // Update user's average rating
        await pool.query(`
            UPDATE users 
            SET seller_rating = (
                SELECT AVG(rating)::DECIMAL(3,2) 
                FROM ratings 
                WHERE user_id = $1
            )
            WHERE user_id = $1
        `, [user_id]);

        res.json(newRating.rows[0]);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Get user ratings
app.get('/api/ratings/:userId', async (req, res) => {
    try {
        const ratings = await pool.query(`
            SELECT r.*, u.username as rated_by_username 
            FROM ratings r 
            JOIN users u ON r.rated_by = u.user_id 
            WHERE r.user_id = $1
        `, [req.params.userId]);
        res.json(ratings.rows);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});
// Delete product
app.delete('/api/products/:id', isAuthenticated, async (req, res) => {
    try {
        const { id } = req.params;
        
        // Check if user owns the product
        const product = await pool.query(
            "SELECT * FROM products WHERE product_id = $1 AND seller_id = $2",
            [id, req.user.user_id]
        );

        if (product.rows.length === 0) {
            return res.status(403).json("Not authorized to delete this product");
        }

        await pool.query(
            "DELETE FROM products WHERE product_id = $1",
            [id]
        );

        res.json("Product deleted successfully");
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Update product status
app.put('/api/products/:id', isAuthenticated, async (req, res) => {
    try {
        const { id } = req.params;
        const { status } = req.body;
        
        // Check if user owns the product
        const product = await pool.query(
            "SELECT * FROM products WHERE product_id = $1 AND seller_id = $2",
            [id, req.user.user_id]
        );

        if (product.rows.length === 0) {
            return res.status(403).json("Not authorized to update this product");
        }

        const updatedProduct = await pool.query(
            "UPDATE products SET status = $1 WHERE product_id = $2 RETURNING *",
            [status, id]
        );

        res.json(updatedProduct.rows[0]);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});
// Get all products
app.get('/api/products', async (req, res) => {
    try {
        const allProducts = await pool.query(
            "SELECT * FROM products ORDER BY created_at DESC"
        );
        res.json(allProducts.rows);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Get user's listings
app.get('/api/products/my-listings', isAuthenticated, async (req, res) => {
    try {
        const myListings = await pool.query(
            "SELECT * FROM products WHERE seller_id = $1 ORDER BY created_at DESC",
            [req.user.user_id]
        );
        res.json(myListings.rows);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Delete a listing
app.delete('/api/products/:id', isAuthenticated, async (req, res) => {
    try {
        const { id } = req.params;
        
        // Check if user owns the product
        const product = await pool.query(
            "SELECT * FROM products WHERE product_id = $1 AND seller_id = $2",
            [id, req.user.user_id]
        );

        if (product.rows.length === 0) {
            return res.status(403).json("Not authorized to delete this product");
        }

        await pool.query(
            "DELETE FROM products WHERE product_id = $1",
            [id]
        );

        res.json("Product deleted successfully");
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Get cart items
app.get('/api/cart', isAuthenticated, async (req, res) => {
    try {
        const cart = await pool.query(
            `SELECT c.cart_id, c.quantity, p.* 
            FROM cart c 
            JOIN products p ON c.product_id = p.product_id 
            WHERE c.user_id = $1`,
            [req.user.user_id]
        );
        res.json(cart.rows);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Add to cart
app.post('/api/cart/add', isAuthenticated, async (req, res) => {
    try {
        const { product_id, quantity = 1 } = req.body;

        // Check if item already in cart
        const existingItem = await pool.query(
            "SELECT * FROM cart WHERE user_id = $1 AND product_id = $2",
            [req.user.user_id, product_id]
        );

        if (existingItem.rows.length > 0) {
            // Update quantity
            const updatedItem = await pool.query(
                "UPDATE cart SET quantity = quantity + $1 WHERE user_id = $2 AND product_id = $3 RETURNING *",
                [quantity, req.user.user_id, product_id]
            );
            return res.json(updatedItem.rows[0]);
        }

        // Add new item to cart
        const newItem = await pool.query(
            "INSERT INTO cart (user_id, product_id, quantity) VALUES ($1, $2, $3) RETURNING *",
            [req.user.user_id, product_id, quantity]
        );

        res.json(newItem.rows[0]);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Update cart item quantity
app.put('/api/cart/:id', isAuthenticated, async (req, res) => {
    try {
        const { id } = req.params;
        const { quantity } = req.body;

        if (quantity === 0) {
            await pool.query(
                "DELETE FROM cart WHERE cart_id = $1 AND user_id = $2",
                [id, req.user.user_id]
            );
            return res.json({ message: "Item removed from cart" });
        }

        const updatedItem = await pool.query(
            "UPDATE cart SET quantity = $1 WHERE cart_id = $2 AND user_id = $3 RETURNING *",
            [quantity, id, req.user.user_id]
        );

        res.json(updatedItem.rows[0]);
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});

// Remove item from cart
app.delete('/api/cart/:id', isAuthenticated, async (req, res) => {
    try {
        const { id } = req.params;
        await pool.query(
            "DELETE FROM cart WHERE cart_id = $1 AND user_id = $2",
            [id, req.user.user_id]
        );
        res.json({ message: "Item removed from cart" });
    } catch (err) {
        console.error(err.message);
        res.status(500).json("Server Error");
    }
});