import { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        checkUser();
    }, []);

    const checkUser = async () => {
        try {
            const response = await axios.get('http://localhost:5000/auth/user', {
                withCredentials: true
            });
            if (response.status === 200) {
                console.log("AuthContext user:", response.data); // Debug log
                setUser(response.data);
            }
        } catch (error) {
            console.error('Error checking authentication:', error);
            setUser(null);
        } finally {
            setLoading(false);
        }
    };

    const logout = async () => {
    try {
        await axios.post(
            'http://localhost:5000/auth/logout',
            {},
            { withCredentials: true }
        );

        setUser(null);

    } catch (error) {
        console.error('Error logging out:', error);
    }
};


    


    const value = {
        user,
        loading,
        logout,
        checkUser
    };

    return (
        <AuthContext.Provider value={value}>
            { children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}