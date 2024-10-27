import Image from 'next/image';
import { Poppins } from 'next/font/google';

const poppins = Poppins({
  subsets: ['latin'],
  weight: '600',
})

const poppinsR = Poppins({
    subsets: ['latin'],
    weight: '400',
    })

export function ArticleHome() {
  return (
    <div className='mx-36 flex justify-center flex-col'>
        <div className='mt-6 mb-20'>
          <h4 className='text-xl text-blue-800 underline underline-offset-8'>
            Articles
          </h4>
            <div className='flex flex-wrap mt-12 justify-center gap-5'>
                
              <a href='/article1' className='bg-gray-200 w-full sm:w-[49%] h-fit pb-20 rounded-xl'>
                <Image src="/articledummy.png" alt='Article 1' width={600} height={500} className='mx-auto'/>
                <h1 className={`${poppins.className} ml-4 mt-4 text-xl`}>
                     Cupang Giant
                </h1>
                <p className={`${poppinsR.className} mx-4 text-xs mt-4 text-gray-500`}>
                    Lorem ipsum dolor sit amet consectetur adipisicing elit. Veniam labore dolores similique magnam quidem vero libero exercitationem, illum, nobis corporis quaerat necessitatibus reiciendis ut tempora asperiores, quis inventore placeat ea!
                    Quasi, corrupti sint cum fugiat consequuntur itaque perspiciatis quod vero optio reprehenderit aliquam! Cupiditate maiores beatae, aliquid itaque pariatur voluptatum et modi nam illo dolore ratione blanditiis esse corrupti sapiente?
                    Quae animi nihil repellat minima velit eos, accusamus assumenda explicabo quas! Perspiciatis optio non, rerum reiciendis sequi sit dicta, ex temporibus illum, officiis a sint ab cupiditate beatae dignissimos enim.
                </p>
              </a>
                            
              <a href='/article2' className='bg-gray-200 w-full sm:w-[49%] h-fit pb-20 rounded-xl'>
                <Image src="/articledummy.png" alt='Article 1' width={600} height={500} className='mx-auto'/>
                <h1 className={`${poppins.className} ml-4 mt-4 text-xl`}>
                     Cupang Giant
                </h1>
                <p className={`${poppinsR.className} mx-4 text-xs mt-4 text-gray-500`}>
                    Lorem ipsum dolor sit amet consectetur adipisicing elit. Veniam labore dolores similique magnam quidem vero libero exercitationem, illum, nobis corporis quaerat necessitatibus reiciendis ut tempora asperiores, quis inventore placeat ea!
                    Quasi, corrupti sint cum fugiat consequuntur itaque perspiciatis quod vero optio reprehenderit aliquam! Cupiditate maiores beatae, aliquid itaque pariatur voluptatum et modi nam illo dolore ratione blanditiis esse corrupti sapiente?
                    Quae animi nihil repellat minima velit eos, accusamus assumenda explicabo quas! Perspiciatis optio non, rerum reiciendis sequi sit dicta, ex temporibus illum, officiis a sint ab cupiditate beatae dignissimos enim.
                </p>
              </a>
                            
              
          </div>
        </div>
      </div>

  );
}