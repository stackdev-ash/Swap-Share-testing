// src/pages/sell.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../components/auth/AuthContext';

const Sell = () => {
    const navigate = useNavigate();
    const { user, loading: authLoading } = useAuth();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const [formData, setFormData] = useState({
        name: '',
        description: '',
        price: '',
        size: '',
        condition: 'New',
        brand: '',
        original_price: '',
        category: 'Clothing',
        gender: 'Unisex',
        age_group: 'Adult',
        image_url: ''
    });

    useEffect(() => {
        if (!authLoading && !user) {
            navigate('/login');
        }
    }, [user, authLoading, navigate]);

    if (authLoading) {
    return (
        <div className="flex justify-center items-center py-20">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
    );
}


    const conditions = ['New', 'Like New', 'Good', 'Fair', 'Poor'];
    const categories = ['Clothing', 'Shoes', 'Accessories', 'Bags', 'Other'];
    const genders = ['Men', 'Women', 'Unisex'];
    const ageGroups = ['Adult', 'Teen', 'Kids', 'Baby'];
    const sizes = ['XS', 'S', 'M', 'L', 'XL', 'XXL', 'Other'];

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const response = await axios.post(
                'http://localhost:5000/api/products/sell',
                formData,
                { withCredentials: true }
            );

            if (response.data) {
                navigate('/my-listings');
            }
        } catch (err) {
            setError(err.response?.data || 'Error listing product');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto p-6">
            <h2 className="text-2xl font-bold mb-6">List Your Item for Sale</h2>
            
            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-6">

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Item Name *
                    </label>
                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                        className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Description *
                    </label>
                    <textarea
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        required
                        rows="4"
                        className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                    />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Selling Price (₹) *
                        </label>
                        <input
                            type="number"
                            name="price"
                            value={formData.price}
                            onChange={handleChange}
                            required
                            min="0"
                            className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Original Price (₹)
                        </label>
                        <input
                            type="number"
                            name="original_price"
                            value={formData.original_price}
                            onChange={handleChange}
                            min="0"
                            className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Brand
                        </label>
                        <input
                            type="text"
                            name="brand"
                            value={formData.brand}
                            onChange={handleChange}
                            className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Size *
                        </label>
                        <select
                            name="size"
                            value={formData.size}
                            onChange={handleChange}
                            required
                            className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                        >
                            <option value="">Select Size</option>
                            {sizes.map(size => (
                                <option key={size} value={size}>{size}</option>
                            ))}
                        </select>
                    </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Condition *
                        </label>
                        <select
                            name="condition"
                            value={formData.condition}
                            onChange={handleChange}
                            required
                            className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                        >
                            {conditions.map(condition => (
                                <option key={condition} value={condition}>{condition}</option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Category *
                        </label>
                        <select
                            name="category"
                            value={formData.category}
                            onChange={handleChange}
                            required
                            className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                        >
                            {categories.map(category => (
                                <option key={category} value={category}>{category}</option>
                            ))}
                        </select>
                    </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Gender
                    </label>
                    <select
                        name="gender"
                        value={formData.gender}
                        onChange={handleChange}
                        className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                    >
                        {genders.map(gender => (
                            <option key={gender} value={gender}>{gender}</option>
                        ))}
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Age Group
                    </label>
                    <select
                        name="age_group"
                        value={formData.age_group}
                        onChange={handleChange}
                        className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                    >
                        {ageGroups.map(age => (
                            <option key={age} value={age}>{age}</option>
                        ))}
                    </select>
                </div>
            </div>



                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Image URL *
                    </label>
                    <input
                        type="url"
                        name="image_url"
                        value={formData.image_url}
                        onChange={handleChange}
                        required
                        className="w-full p-2 border rounded focus:ring-blue-500 focus:border-blue-500"
                        placeholder="https://example.com/image.jpg"
                    />
                </div>

                <button
                    type="submit"
                    disabled={loading}
                    className={`w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 ${
                        loading ? 'opacity-50 cursor-not-allowed' : ''
                    }`}
                >
                    {loading ? 'Listing...' : 'List Item'}
                </button>
            </form>
        </div>
    );
};

export default Sell;
