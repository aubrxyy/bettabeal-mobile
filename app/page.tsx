'use client'

import { useRouter } from 'next/navigation';
import { setCookie } from './utils/cookies';
import { NewArrival } from './_components/NewArrival';
import { Hero } from './_components/Hero';
import { ArticleHome } from './_components/ArticleHome';

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
      <Hero />
      <NewArrival />
      <ArticleHome />
      <div className="py-2">
        <button onClick={handleLogout} className="bg-red-500 text-white p-2 rounded flex mx-auto">Logout</button>
      </div>
      
    </>
  );
}