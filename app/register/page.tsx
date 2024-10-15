import Image from "next/image";
import { Inter } from 'next/font/google';

const inter = Inter({
  subsets: ['latin'],
  weight: '700',
})

const interR = Inter({
  subsets: ['latin'],
  weight: '400',
})

export default function register() {
    return (
        <div className="bg-cover bg-center h-screen loginBG">
            <div className="flex items-center justify-center h-full">
                <div className="bg-white px-16 py-20 rounded-lg shadow-lg text-center">
                    <h2 className={`text-3xl mb-2 text-black ${inter.className}`}>Create an account</h2>
                    <p className={`text-gray-400 mb-16 ${interR.className}`}>Have an account? <a href="/login" className="text-blue-500">Login</a></p>
                    <form id="loginForm">
                        <div className="mb-4 w-96">
                            <label htmlFor="username" className={`block text-left mb-2 text-gray-400 ${interR.className}`}>Username</label>
                            <input type="text" id="username" className="w-full p-2 border border-gray-300 rounded" placeholder="Enter username" required/>
                        </div>
                        <div className="mb-4 w-96">
                            <label htmlFor="Email" className={`block text-left mb-2 text-gray-400 ${interR.className}`}>Email</label>
                            <input type="text" id="Email" className="w-full p-2 border border-gray-300 rounded" placeholder="Enter email" required/>
                        </div>
                        <div className="mb-4">
                            <label htmlFor="password" className={`block text-left mb-2 text-gray-400 ${interR.className}`}>Password    </label>
                            <input type="password" id="password" className="w-full p-2 border border-gray-300 rounded" placeholder="Enter password" required/>
                        </div>
                        <button type="submit"
                            className="w-full bg-[#1E2753] hover:bg-[#49579B] text-white p-2 rounded">Create an account</button>
                    </form>
                    <hr className="mt-8"></hr>
                    <div className="flex justify-center">
                        <Image src="/logoBB.png" width={150} height={100} alt="BettaBeal" className="mt-12"/>
                    </div>
                </div>
            </div>
        </div>
    );
}