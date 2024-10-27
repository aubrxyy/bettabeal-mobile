import Image from 'next/image';
import { Poppins } from 'next/font/google';
import { Rating } from '@mui/material';

const poppinsB = Poppins({
    subsets: ['latin'],
    weight: '700',
    })

const poppins = Poppins({
  subsets: ['latin'],
  weight: '600',
})

const poppinsR = Poppins({
    subsets: ['latin'],
    weight: '400',
    })

export function NewArrival() {
  return (
    <div className='max-sm:mx-4 sm:mx-8 md:mx-12 lg:mx-36 my-6 flex justify-center flex-col'>
        <div className='mt-12'>
          <h4 className='text-xl text-blue-800 underline underline-offset-8'>
            New Arrival
          </h4>
            <div className='flex flex-wrap mt-12 justify-center gap-12'>
                
              <div className='bg-gray-200 w-full  max-sm:w-[90%] md:w-[36%] lg:w-[28%] xl:w-[16%] h-96 rounded-lg'>
                <Image src="/produkfish1.png" alt='Produk 1' width={135} height={200} className='mx-auto mt-6'/>
                <h1 className={`${poppins.className} ml-4 text-base`}>
                     Halfmoon
                </h1>
                <Rating name="read-only" value={5} precision={0.5} size='small' className='ml-3' readOnly />
                <h2 className={`${poppins.className} ml-4 text-xs`}>
                    Price range
                </h2>
                <p className={`${poppinsR.className} ml-4 text-xs mt-1 text-[#0F4A99]`}>
                    Rp 2.000 - 3.000 / ekor
                </p>
                <button className={`${poppinsB.className} text-nowrap text-white bg-[#0F4A99] mt-2 flex rounded-lg px-7 py-[6px] text-sm mx-auto transition-all hover:bg-[#0a356e]`}>
                    Beli Sekarang
                </button>
              </div>
              
              <div className='bg-gray-200 w-full max-sm:w-[90%] md:w-[36%] lg:w-[28%] xl:w-[16%] h-96 rounded-lg'>
                <Image src="/produkfish1.png" alt='Produk 1' width={135} height={200} className='mx-auto mt-6'/>
                <h1 className={`${poppins.className} ml-4 text-base`}>
                     Halfmoon
                </h1>
                <Rating name="read-only" value={4.5} precision={0.5} size='small' className='ml-3' readOnly />
                <h2 className={`${poppins.className} ml-4 text-xs`}>
                    Price range
                </h2>
                <p className={`${poppinsR.className} ml-4 text-xs mt-1 text-[#0F4A99]`}>
                    Rp 2.000 - 3.000 / ekor
                </p>
                <button className={`${poppinsB.className} text-nowrap text-white bg-[#0F4A99] mt-2 flex rounded-lg px-7 py-[6px] text-sm mx-auto transition-all hover:bg-[#0a356e]`}>
                    Beli Sekarang
                </button>
              </div>
              
              <div className='bg-gray-200 w-full max-sm:w-[90%] md:w-[36%] lg:w-[28%] xl:w-[16%] h-96 rounded-lg'>
                <Image src="/produkfish1.png" alt='Produk 1' width={135} height={200} className='mx-auto mt-6'/>
                <h1 className={`${poppins.className} ml-4 text-base`}>
                     Halfmoon
                </h1>
                <Rating name="read-only" value={4.0} precision={0.5} size='small' className='ml-3' readOnly />
                <h2 className={`${poppins.className} ml-4 text-xs`}>
                    Price range
                </h2>
                <p className={`${poppinsR.className} ml-4 text-xs mt-1 text-[#0F4A99]`}>
                    Rp 2.000 - 3.000 / ekor
                </p>
                <button className={`${poppinsB.className} text-nowrap text-white bg-[#0F4A99] mt-2 flex rounded-lg px-7 py-[6px] text-sm mx-auto transition-all hover:bg-[#0a356e]`}>
                    Beli Sekarang
                </button>
              </div>
              
              <div className='bg-gray-200 w-full max-sm:w-[90%] md:w-[36%] lg:w-[28%] xl:w-[16%] h-96 rounded-lg'>
                <Image src="/produkfish1.png" alt='Produk 1' width={135} height={200} className='mx-auto mt-6'/>
                <h1 className={`${poppins.className} ml-4 text-base`}>
                     Halfmoon
                </h1>
                <Rating name="read-only" value={3.5} precision={0.5} size='small' className='ml-3' readOnly />
                <h2 className={`${poppins.className} ml-4 text-xs`}>
                    Price range
                </h2>
                <p className={`${poppinsR.className} ml-4 text-xs mt-1 text-[#0F4A99]`}>
                    Rp 2.000 - 3.000 / ekor
                </p>
                <button className={`${poppinsB.className} text-nowrap text-white bg-[#0F4A99] mt-2 flex rounded-lg px-7 py-[6px] text-sm mx-auto transition-all hover:bg-[#0a356e]`}>
                    Beli Sekarang
                </button>
              </div>
              
              <div className='bg-gray-200 w-full max-sm:w-[90%] md:w-[36%] lg:w-[28%] xl:w-[16%] h-96 rounded-lg'>
                <Image src="/produkfish1.png" alt='Produk 1' width={135} height={200} className='mx-auto mt-6'/>
                <h1 className={`${poppins.className} ml-4 text-base`}>
                     Halfmoon
                </h1>
                <Rating name="read-only" value={3} precision={0.5} size='small' className='ml-3' readOnly />
                <h2 className={`${poppins.className} ml-4 text-xs`}>
                    Price range
                </h2>
                <p className={`${poppinsR.className} ml-4 text-xs mt-1 text-[#0F4A99]`}>
                    Rp 2.000 - 3.000 / ekor
                </p>
                <button className={`${poppinsB.className} text-nowrap text-white bg-[#0F4A99] mt-2 flex rounded-lg px-7 py-[6px] text-sm mx-auto transition-all hover:bg-[#0a356e]`}>
                    Beli Sekarang
                </button>
              </div>
              
          </div>
        </div>
      </div>

  );
}