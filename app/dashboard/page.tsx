'use client'

import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { jwtDecode } from 'jwt-decode'; // Correct import for jwt-decode
import { getCookie } from '../utils/cookies';

interface DecodedToken {
  role: string;
}

export default function Dashboard() {
  const router = useRouter();
  const [isAuthorized, setIsAuthorized] = useState(false);

  useEffect(() => {
    const currentUser = getCookie('currentUser');
    if (!currentUser) {
      router.push('/login');
      return;
    }

    try {
      const decodedToken: DecodedToken = jwtDecode(currentUser);
      if (decodedToken.role !== 'seller') {
        router.push('/login');
      } else {
        setIsAuthorized(true);
      }
    } catch (error) {
      console.error('Invalid token:', error);
      router.push('/login');
    }
  }, [router]);

  if (!isAuthorized) {
    return null; 
  }

  return (
    <div>
      "dashboard"
    </div>
  );
}