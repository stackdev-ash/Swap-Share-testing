// src/pages/cart.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../components/auth/AuthContext';

const Cart = () => {
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { user , loading:cheetahloading } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (cheetahloading) return;
        if (!user) {
            navigate('/login');
            return;
        }
        fetchCartItems();
    }, [user, cheetahloading, navigate]);

    const fetchCartItems = async () => {
        try {
            const response = await axios.get('http://localhost:5000/api/cart', {
                withCredentials: true
            });
            setCartItems(response.data);
            setLoading(false);
        } catch (err) {
            setError('Failed to fetch cart items');
            setLoading(false);
        }
    };

    const updateQuantity = async (cartId, newQuantity) => {
        try {
            await axios.put(`http://localhost:5000/api/cart/${cartId}`, 
                { quantity: newQuantity },
                { withCredentials: true }
            );
            fetchCartItems(); // Refresh cart items
        } catch (err) {
            setError('Failed to update quantity');
        }
    };

    const removeItem = async (cartId) => {
        try {
            await axios.delete(`http://localhost:5000/api/cart/${cartId}`, {
                withCredentials: true
            });
            fetchCartItems(); // Refresh cart items
        } catch (err) {
            setError('Failed to remove item');
        }
    };

    const calculateTotal = () => {
        return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0).toFixed(2);
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div className="text-red-500">{error}</div>;

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <h1 className="text-3xl font-bold mb-8 text-blue-600">Shopping Cart</h1>
            
            {cartItems.length === 0 ? (
                <div className="text-center py-8">
                    <p className="text-gray-500 mb-4">Your cart is empty</p>
                    <button
                        onClick={() => navigate('/shop')}
                        className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
                    >
                        Continue Shopping
                    </button>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {/* Cart Items */}
                    <div className="md:col-span-2  text-black">
                        {cartItems.map(item => (
                            <div key={item.cart_id} className="bg-white rounded-lg shadow mb-4 p-4">
                                <div className="flex items-center">
                                    <img
                                        src={item.image_url}
                                        alt={item.name}
                                        className="w-24 h-24 object-cover rounded"
                                    />
                                    <div className="ml-4 flex-grow">
                                        <h3 className="text-lg font-semibold">{item.name}</h3>
                                        <p className="text-gray-600">₹{item.price}</p>
                                        <div className="flex items-center mt-2">
                                            <button
                                                onClick={() => updateQuantity(item.cart_id, Math.max(0, item.quantity - 1))}
                                                className="bg-blue-200 px-3 py-1 rounded-l"
                                            >
                                                -
                                            </button>
                                            <span className="px-4 py-1 bg-gray-100">
                                                {item.quantity}
                                            </span>
                                            <button
                                                onClick={() => updateQuantity(item.cart_id, item.quantity + 1)}
                                                className="bg-blue-200 px-3 py-1 rounded-r"
                                            >
                                                +
                                            </button>
                                            <button
                                                onClick={() => removeItem(item.cart_id)}
                                                className="ml-4 bg-blue-600 text-white hover:text-red-300"
                                            >
                                                Remove
                                            </button>
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <p className="font-semibold">
                                            ₹{(item.price * item.quantity).toFixed(2)}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* Cart Summary */}
                    <div className="bg-black rounded-lg shadow  p-4 h-fit">
                        <h2 className="text-xl font-semibold mb-4">Order Summary</h2>
                        <div className="space-y-2">
                            <div className="flex justify-between">
                                <span>Subtotal</span>
                                <span>₹{calculateTotal()}</span>
                            </div>
                            <div className="flex justify-between">
                                <span>Shipping</span>
                                <span>₹150.00</span>
                            </div>
                            <div className="border-t pt-2 mt-2">
                                <div className="flex justify-between font-semibold">
                                    <span>Total</span>
                                    <span>₹{(parseFloat(calculateTotal()) + 150).toFixed(2)}</span>
                                </div>
                            </div>
                        </div>
                        <button
                            onClick={() => navigate('/checkout')}
                            className="w-full bg-blue-600 text-white py-2 rounded mt-4 hover:bg-blue-700"
                        >
                            Proceed to Checkout
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Cart;