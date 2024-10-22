'use client'

import "./_components/Header"
import Header from "./_components/Header"
import { useRouter } from 'next/navigation';
import { setCookie } from './utils/cookies';

export default function Home() {
  const router = useRouter();

  const handleLogout = () => {
    // Clear cookies
    setCookie('currentUser', '', -1);
    setCookie('userRole', '', -1);
    // Clear local storage
    localStorage.removeItem('token');
    // Redirect to login page
    router.push('/login');
  };

  return (
    <>
      <Header />
      <div className="gradient-bg w-full h-[67.5vh]"></div>
      <div className="pt-4">
        <button onClick={handleLogout} className="bg-red-500 text-white p-2 rounded flex mx-auto">Logout</button>
      </div>
    </>
  );
}
