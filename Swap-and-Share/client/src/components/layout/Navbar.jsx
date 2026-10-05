// src/components/layout/navbar.jsx
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/', { replace: true });}
  

  return (
    <nav className="bg-white sticky top-0 z-50 shadow-md">
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16 items-center">
          <Link to="/" className="text-4xl font-bold text-blue-600">
            Swap&Share
          </Link>

          <div className="hidden md:flex space-x-8">
            <Link to="/" className="text-gray-700 hover:text-blue-600">Home</Link>
            <Link to="/shop" className="text-gray-700 hover:text-blue-600">Inventory</Link>
            <Link to="/sell" className="text-gray-700 hover:text-blue-600">Sell/Donate</Link>
            <Link to="/my-listings" className="text-gray-700 hover:text-blue-600">My Listings</Link>
          </div>

          <div className="flex items-center space-x-4">
            <Link to="/cart" className="text-gray-700 hover:text-blue-600">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </Link>

            {user ? (
              <div className="flex items-center space-x-4">
                <img
                src={
                  user?.profile_picture
                    ? user.profile_picture
                    : `https://ui-avatars.com/api/?name=${encodeURIComponent(user?.username || "User")}`
                }
                alt={user?.username || "Profile"}
                className="h-14 w-14 rounded-full object-cover border-2 border-blue-600 bg-gray-200"
                onError={(e) => {
                  e.currentTarget.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(user?.username || "User")}`;
                }}
/>

               <button onClick={handleLogout} className="bg-white text-gray-700 hover:text-blue-600">
                    Logout
               </button>
              </div>
            ) : (
              <Link to="/login" className="text-gray-700 hover:text-blue-600">
                Login
              </Link>
            )}
          </div>
          
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
  