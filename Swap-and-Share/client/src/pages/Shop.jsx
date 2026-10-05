// src/pages/shop.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../components/auth/AuthContext';

const Shop = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [addingToCart, setAddingToCart] = useState(null);
    const { user } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await axios.get('http://localhost:5000/api/products');
                setProducts(response.data);
                setLoading(false);
            } catch (err) {
                setError('Failed to fetch products');
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    const handleAddToCart = async (productId) => {
        if (!user) {
            navigate('/login');
            return;
        }

        try {
            setAddingToCart(productId);
            await axios.post('http://localhost:5000/api/cart/add', 
                { product_id: productId, quantity: 1 },
                { withCredentials: true }
            );
            // Show success message
            alert('Added to cart successfully!');
        } catch (err) {
            alert('Failed to add to cart');
        } finally {
            setAddingToCart(null);
        }
    };

    if (loading) return (
        <div className="flex justify-center items-center min-h-screen">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
    );

    if (error) return (
        <div className="text-center text-red-600 p-4">
            {error}
        </div>
    );

    return (
        <div className="max-w-7xl mx-auto px-4">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-blue-600">Shop</h1>
                {user && (
                    <button
                        onClick={() => navigate('/cart')}
                        className="bg-gray-100 text-gray-800 px-4 py-2 rounded hover:bg-gray-200"
                    >
                        View Cart
                    </button>
                )}
            </div>

            {products.length === 0 ? (
                <div className="text-center py-8 text-gray-600">
                    No products available at the moment.
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {products.map(product => (
                        <div key={product.product_id} className="bg-white rounded-lg shadow-md overflow-hidden">
                            <img 
                                src={product.image_url} 
                                alt={product.name}
                                className="w-full h-48 object-cover"
                                onError={(e) => {
                                    e.target.onerror = null;
                                    e.target.src = 'https://via.placeholder.com/400x300?text=No+Image';
                                }}
                            />
                            <div className="p-4">
                                <h3 className="text-lg font-semibold text-gray-600">{product.name}</h3>
                                <p className="text-gray-600">₹{product.price}</p>
                                <p className="text-sm text-gray-500 mb-2">
                                    {product.size} • {product.condition}
                                </p>
                                <p className="text-sm text-gray-500 mb-4">
                                    {product.brand && `Brand: ${product.brand}`}
                                </p>
                                <button 
                                    onClick={() => handleAddToCart(product.product_id)}
                                    disabled={addingToCart === product.product_id}
                                    className={`w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 
                                        ${addingToCart === product.product_id ? 'opacity-50 cursor-not-allowed' : ''}`}
                                >
                                    {addingToCart === product.product_id ? 'Adding...' : 'Add to Cart'}
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Shop;