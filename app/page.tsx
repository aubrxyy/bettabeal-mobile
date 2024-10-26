'use client'

import { useRouter } from 'next/navigation';
import "./_components/Header";
import Header from "./_components/Header";
import { setCookie } from './utils/cookies';
import { NewArrival } from './_components/NewArrival';
import { Hero } from './_components/Hero';

export default function Home() {
  const router = useRouter();

  const handleLogout = () => {
    setCookie('currentUser', '', -1);
    setCookie('userRole', '', -1);
    localStorage.removeItem('token');
    router.push('/login');
  };

  return (
    <>
      <Header />
      <Hero />
      <NewArrival />
      
      <div className="pt-4">
        <button onClick={handleLogout} className="bg-red-500 text-white p-2 rounded flex mx-auto">Logout</button>
      </div>
    </>
  );
}