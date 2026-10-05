import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./components/auth/AuthContext";
import Navbar from "./components/layout/Navbar";
import Sell from "./pages/Sell";  // Make sure this matches your file name case
import Footer from "./components/layout/Footer";
import Home from "./pages/Home";
import Shop from "./pages/Shop";
import Login from "./components/auth/Login";
import Cart from "./pages/Cart";
import MyListings from "./pages/MyListings";

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen flex flex-col bg-gray-50">
          <Navbar />
          <main className="flex-grow container mx-auto px-4 py-8">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/shop" element={<Shop />} />
              <Route path="/login" element={<Login />} />
              <Route path="/cart" element={<Cart />} />
              <Route path="/my-listings" element={<MyListings />} />
              <Route path="/sell" element={<Sell />} />  {/* Changed from SellProduct to Sell */}
              <Route path="/my-listings" element={<MyListings />} />

            </Routes>
          </main>
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;