import { Link } from 'react-router-dom';
import bgImage from '../assets/img1.jpg';

const Home = () => {
  return (
    <div className="bg-gray-50 min-h-screen ">

      <div className="relative max-w-7xl mx-auto  mt-12 rounded-xl overflow-hidden">

      {/* Background Image */}
      <div
        className="h-[500px] bg-cover bg-center"
        style={{ backgroundImage: `url(${bgImage})` }}
      ></div>

      {/* Overlay Mask */}
      <div className="absolute inset-0 bg-black/20 backdrop-blur-sm flex items-center justify-center">

        <div className="text-center px-6">
          <h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold text-white">
            Sustainable Style, Endless Finds
          </h1>

          <p className="mt-4 text-gray-200 text-lg md:text-xl max-w-2xl mx-auto">
            Find unique second-hand clothing at amazing prices.
          </p>

          <div className="mt-6">
            <Link
              to="/shop"
              className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-md font-medium"
            >
              Shop Now
            </Link>
          </div>
        </div>

      </div>
      

    </div>

    {/* Impact Section */}
<section className="max-w-7xl mx-auto px-6 py-20">
  
  <div className="text-center mb-14">
    <h2 className="text-3xl md:text-4xl font-bold text-gray-900">
      The Impact of Fast Fashion
    </h2>
    <p className="mt-4 text-gray-600 max-w-2xl mx-auto">
      The fashion industry is one of the largest polluters in the world.
      Here's why sustainable choices matter.
    </p>
  </div>

  <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-4">

    {/* Card 1 */}
    <div className="bg-white p-8 rounded-2xl shadow-md hover:shadow-xl transition">
      <h3 className="text-4xl font-extrabold text-blue-600">92M+</h3>
      <p className="mt-3 text-gray-700">
        Tons of textile waste end up in landfills every year.
      </p>
    </div>

    {/* Card 2 */}
    <div className="bg-white p-8 rounded-2xl shadow-md hover:shadow-xl transition">
      <h3 className="text-4xl font-extrabold text-blue-600">2,700L</h3>
      <p className="mt-3 text-gray-700">
        Water required to produce a single cotton T-shirt.
      </p>
    </div>

    {/* Card 3 */}
    <div className="bg-white p-8 rounded-2xl shadow-md hover:shadow-xl transition">
      <h3 className="text-4xl font-extrabold text-blue-600">10%</h3>
      <p className="mt-3 text-gray-700">
        Of global carbon emissions come from fashion.
      </p>
    </div>

    {/* Card 4 */}
    <div className="bg-white p-8 rounded-2xl shadow-md hover:shadow-xl transition">
      <h3 className="text-4xl font-extrabold text-blue-600">1 Resale</h3>
      <p className="mt-3 text-gray-700">
        Extending a garment’s life reduces its carbon footprint by up to 30%.
      </p>
    </div>

  </div>
</section>

  </div>


  );
};

export default Home;
