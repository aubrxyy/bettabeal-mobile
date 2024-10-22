'use client'

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { getCookie, setCookie } from '../utils/cookies';

export default function Dashboard() {
  const router = useRouter();
  const [isAuthorized, setIsAuthorized] = useState(false);

  useEffect(() => {
    const currentUser = getCookie('currentUser');
    const userRole = getCookie('userRole');
    console.log('Current User:', currentUser); // Debugging log
    console.log('User Role:', userRole); // Debugging log

    if (!currentUser || userRole === 'customer' || userRole === undefined) {
      console.log('User is not logged in or role is customer/undefined, redirecting to /error');
      router.push('/error'); // Redirect to an error page or show an error message
      return;
    }

    if (userRole === 'seller') {
      console.log('User role is seller, setting isAuthorized to true');
      setIsAuthorized(true);
    } else {
      console.log('User role is not recognized, redirecting to /error');
      router.push('/error');
    }
  }, [router]);

  const handleLogout = () => {
    // Clear cookies
    setCookie('currentUser', '', -1);
    setCookie('userRole', '', -1);
    // Clear local storage
    localStorage.removeItem('token');
    // Redirect to login page
    router.push('/login');
  };

  if (!isAuthorized) {
    return null; // Or a loading spinner
  }

  return (
    <div>
      <h1>Dashboard</h1>
      <button onClick={handleLogout} className="bg-red-500 text-white p-2 rounded">Logout</button>
    </div>
  );
}