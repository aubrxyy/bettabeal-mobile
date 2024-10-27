import Image from 'next/image';
import { Inter } from 'next/font/google';

const inter = Inter({
    subsets: ['latin'],
    weight: '600',
    })

export function Footer() {
  return (
    <footer className="bg-[#0F4A99] text-white">
      <div className="container mx-auto py-32 px-8">
        <div className="flex justify-start">
          <div>
            <Image src="/logowhite.png" alt="Logo" width={150} height={150} />
            <p className='w-[50ch] text-justify text-sm mt-6 ml-6 leading-7'>
                Temukan keindahan dan keunikan ikan cupang terbaik di sini. Kami menghadirkan koleksi ikan cupang berkualitas dengan variasi warna memukau dan karakter yang kuat. Dari penggemar pemula hingga kolektor berpengalaman, Bettabeal adalah tempat yang tepat untuk mempercantik akuarium Anda.
            </p>
            <div className='flex flex-row mt-12 ml-6 space-x-8'>
                <a href="/">
                    <Image src="/X-Logo.png" alt="Twitter" width={20} height={20}/>
                </a>
                <a href="/">
                    <Image src="/fb-logo.png" alt="Facebook" width={20} height={20}/>
                </a>
                <a href="/">
                    <Image src="/ig-logo.png" alt="Instagram" width={20} height={20}/>
                </a>
                <a href="/">
                    <Image src="/wa-logo.png" alt="WhatsApp" width={20} height={20}/>
                </a>
            </div>
            
          
          </div>
          <div>
            <p className={`${inter.className} ml-60`}>Servis</p>
            <ul className='ml-60 mt-4'>
                <li className='mt-4 text-sm text-gray-400 hover:text-white hover:underline underline-offset-4 transition-all'><a href="/">Informasi Ikan</a></li>
                <li className='mt-4 text-sm text-gray-400 hover:text-white hover:underline underline-offset-4 transition-all'><a href="/">Artikel Ikan</a></li>
                <li className='mt-4 text-sm text-gray-400 hover:text-white hover:underline underline-offset-4 transition-all'><a href="/">Pembayaran</a></li>
            </ul>
          </div>
        </div>
      </div>
    </footer>
  );
}