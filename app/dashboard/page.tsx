'use client'

import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { getCookie } from '../utils/cookies';
import Header from './Header';
import Navbar from './Navbar';

interface Seller {
  email: string;
}

export default function Dashboard() {
  const router = useRouter();
  const [userData, setUserData] = useState<Seller | null>(null);
  const [userId, setUserId] = useState<string | null>(null);

  useEffect(() => {
    const id = getCookie('userId');
    console.log('Retrieved User ID:', id);
    setUserId(id);
  }, []);

  useEffect(() => {
    if (userId) {
      fetch(`https://api-bettabeal.dgeo.id/api/sellers/${userId}`)
        .then(response => response.json())
        .then(data => setUserData(data.seller))
        .catch(error => console.error('Error fetching user data:', error));
    }
  }, [userId]);

  useEffect(() => {
    if (!userData) {
      router.push('/error');
    }
  }, [userData, router]);

  if (!userData) {
    return null;
  }

  return (
    <div className="flex">
      <Header />
      <Navbar />
      <div className="flex-1" style={{ background: 'linear-gradient(to right, #1DACFE 45%, #7ec9f2 94%)' }}>
        <div className="p-4 mt-[4.63rem]">
          Dashboard
          {userData && (
        <span className="ml-4 text-gray-700">
          {userData.email}
        </span>
      )}
        </div>
      </div>
    </div>
  );
}