import { useState } from 'react';
import axios from 'axios';

const UserRating = ({ userId, onRatingSubmit }) => {
    const [rating, setRating] = useState(5);
    const [comment, setComment] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post('http://localhost:5000/api/ratings', {
                user_id: userId,
                rating,
                comment
            }, { withCredentials: true });
            
            if (onRatingSubmit) onRatingSubmit();
            setComment('');
        } catch (error) {
            console.error('Error submitting rating:', error);
        }
    };

    return (
        <div className="max-w-md mx-auto p-4">
            <h3 className="text-lg font-semibold mb-4">Rate this Seller</h3>
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block mb-2">Rating</label>
                    <select
                        value={rating}
                        onChange={(e) => setRating(Number(e.target.value))}
                        className="w-full p-2 border rounded"
                    >
                        {[5,4,3,2,1].map(num => (
                            <option key={num} value={num}>{num} Stars</option>
                        ))}
                    </select>
                </div>
                <div>
                    <label className="block mb-2">Comment</label>
                    <textarea
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                        className="w-full p-2 border rounded"
                        rows="3"
                    ></textarea>
                </div>
                <button
                    type="submit"
                    className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
                >
                    Submit Rating
                </button>
            </form>
        </div>
    );
};

export default UserRating;