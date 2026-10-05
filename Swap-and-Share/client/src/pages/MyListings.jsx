import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../components/auth/AuthContext';

const MyListings = () => {
    const [listings, setListings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { user } = useAuth();

    // Fetch user's listings
    useEffect(() => {
        const fetchListings = async () => {
            try {
                const response = await axios.get('http://localhost:5000/api/products/my-listings', {
                    withCredentials: true
                });
                setListings(response.data);
                setLoading(false);
            } catch (err) {
                setError('Failed to fetch listings');
                setLoading(false);
            }
        };

        fetchListings();
    }, []);

    // Delete listing
    const handleDelete = async (productId) => {
        if (window.confirm('Are you sure you want to delete this listing?')) {
            try {
                await axios.delete(`http://localhost:5000/api/products/${productId}`, {
                    withCredentials: true
                });
                setListings(listings.filter(listing => listing.product_id !== productId));
            } catch (err) {
                setError('Failed to delete listing');
            }
        }
    };

    // Update listing status
    const handleStatusUpdate = async (productId, newStatus) => {
        try {
            await axios.put(`http://localhost:5000/api/products/${productId}`, {
                status: newStatus
            }, {
                withCredentials: true
            });
            setListings(listings.map(listing => 
                listing.product_id === productId 
                    ? { ...listing, status: newStatus }
                    : listing
            ));
        } catch (err) {
            setError('Failed to update status');
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
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-900">My Listings</h1>
                <Link
                    to="/sell"
                    className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
                >
                    Add New Listing
                </Link>
            </div>

            {listings.length === 0 ? (
                <div className="text-center py-12">
                    <p className="text-gray-600 mb-4">You haven't listed any items yet.</p>
                    <Link
                        to="/sell"
                        className="text-blue-600 hover:text-blue-800"
                    >
                        Start selling now
                    </Link>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {listings.map(listing => (
                        <div 
                            key={listing.product_id}
                            className="bg-white rounded-lg shadow-md overflow-hidden"
                        >
                            <img
                                src={listing.image_url || 'https://via.placeholder.com/300'}
                                alt={listing.name}
                                className="w-full h-48 object-cover"
                            />
                            <div className="p-4">
                                <h3 className="text-lg font-semibold mb-2 text-gray-600">{listing.name}</h3>
                                <p className="text-gray-600 mb-2">₹{listing.price}</p>
                                <p className="text-sm text-gray-500 mb-2">
                                    {listing.size} • {listing.condition}
                                </p>
                                <div className="flex items-center justify-between mb-4">
                                    <span className={`px-2 py-1 rounded text-sm ${
                                        listing.status === 'available' 
                                            ? 'bg-green-100 text-green-800'
                                            : 'bg-gray-100 text-gray-800'
                                    }`}>
                                        {listing.status}
                                    </span>
                                    <select
                                        value={listing.status}
                                        onChange={(e) => handleStatusUpdate(listing.product_id, e.target.value)}
                                        className="text-sm border rounded p-1"
                                    >
                                        <option value="available">Available</option>
                                        <option value="sold">Sold</option>
                                        <option value="reserved">Reserved</option>
                                    </select>
                                </div>
                                <div className="flex justify-between">
                                    <Link
                                        to={`/edit-product/${listing.product_id}`}
                                        className="text-blue-600 hover:text-blue-800"
                                    >
                                        Edit
                                    </Link>
                                    <button
                                        onClick={() => handleDelete(listing.product_id)}
                                        className="text-red-600 hover:text-red-800"
                                    >
                                        Delete
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default MyListings;