package nav.enro.example.data

import java.util.*

data class SimpleData(
    val id: String,
    val ownerId: String,
    val isPublic: Boolean,
    val title: String,
    val description: String
)

val users = listOf(
    "Isaac",
    "Bob",
    "Mary"
)

private val messages = listOf(
    "The quick brown fox jumps over the lazy dog.",
    "She opened up her third bottle of wine of the night.",
    "Writing a list of random sentences is harder than I initially thought it would be.",
    "She looked into the mirror and saw another person.",
    "All they could see was the blue water surrounding their sailboat.",
    "She had some amazing news to share but nobody to share it with.",
    "Don't piss in my garden and tell me you're trying to help my plants grow.",
    "Of course, she loves her pink bunny slippers.",
    "Truth in advertising and dinosaurs with skateboards have much in common.",
    "The rain pelted the windshield as the darkness engulfed us.",
    "As he looked out the window, he saw a clown walk by.",
    "The efficiency we have at removing trash has made creating trash more acceptable.",
    "Twin 4-month-olds slept in the shade of the palm tree while the mother tanned in the sun.",
    "There's an art to getting your way, and spitting olive pits across the table isn't it.",
    "I met an interesting turtle while the song on the radio blasted away.",
    "Check back tomorrow; I will see if the book has arrived.",
    "He walked into the basement with the horror movie from the night before playing in his head.",
    "Smoky the Bear secretly started the fires.",
    "The body piercing didn't go exactly as he expected.",
    "One small action would change her life, but whether it would be for better or for worse was yet to be determined.",
    "The crowd yells and screams for more memes.",
    "The small white buoys marked the location of hundreds of crab pots.",
    "Iron pyrite is the most foolish of all minerals.",
    "He barked orders at his daughters but they just stared back with amusement.",
    "Car safety systems have come a long way, but he was out to prove they could be outsmarted.",
    "When I was little I had a car door slammed shut on my hand and I still remember it quite vividly.",
    "We have young kids who often walk into our room at night for various reasons including clowns in the closet.",
    "Chocolate covered crickets were his favorite snack.",
    "I love bacon, beer, birds, and baboons.",
    "They looked up at the sky and saw a million stars.",
    "He didn't understand why the bird wanted to ride the bicycle.",
    "He wondered if she would appreciate his toenail collection.",
    "He had unknowingly taken up sleepwalking as a nighttime hobby.",
    "He wondered if it could be called a beach if there was no sand.",
    "The blue parrot drove by the hitchhiking mongoose.",
    "There's a message for you if you look up.",
    "He is no James Bond; his name is Roger Moore.",
    "They're playing the piano while flying in the plane.",
    "Garlic ice-cream was her favorite.",
    "We should play with legos at camp.",
    "This book is sure to liquefy your brain.",
    "Had he known what was going to happen, he would have never stepped into the shower.",
    "Greetings from the galaxy MACS0647-JD, or what we call home.",
    "It took him a month to finish the meal.",
    "She saw the brake lights, but not in time.",
    "I would be delighted if the sea were full of cucumber juice.",
    "As the rental car rolled to a stop on the dark road, her fear increased by the moment.",
    "If you like tuna and tomato sauce- try combining the two. It’s really not as bad as it sounds.",
    "Happiness can be found in the depths of chocolate pudding.",
    "He wasn't bitter that she had moved on but from the radish.",
    "The sun had set and so had his dreams.",
    "They wandered into a strange Tiki bar on the edge of the small beach town.",
    "There were white out conditions in the town; subsequently, the roads were impassable.",
    "Separation anxiety is what happens when you can't find your phone.",
    "He used to get confused between soldiers and shoulders, but as a military man, he now soldiers responsibility.",
    "She had some amazing news to share but nobody to share it with.",
    "The paintbrush was angry at the color the artist chose to use.",
    "He had unknowingly taken up sleepwalking as a nighttime hobby.",
    "Even though he thought the world was flat he didn’t see the irony of wanting to travel around the world.",
    "Had he known what was going to happen, he would have never stepped into the shower.",
    "It was her first experience training a rainbow unicorn.",
    "Rock music approaches at high velocity.",
    "Everyone was curious about the large white blimp that appeared overnight.",
    "There are few things better in life than a slice of pie.",
    "He didn't understand why the bird wanted to ride the bicycle.",
    "Greetings from the real universe.",
    "When he had to picnic on the beach, he purposely put sand in other people’s food.",
    "She had a habit of taking showers in lemonade.",
    "Italy is my favorite country; in fact, I plan to spend two weeks there next year.",
    "It must be five o'clock somewhere.",
    "The old apple revels in its authority.",
    "Chocolate covered crickets were his favorite snack.",
    "Pair your designer cowboy hat with scuba gear for a memorable occasion.",
    "The mysterious diary records the voice.",
    "I was very proud of my nickname throughout high school but today- I couldn’t be any different to what my nickname was.",
    "She says she has the ability to hear the soundtrack of your life.",
    "If I don’t like something, I’ll stay away from it.",
    "25 years later, she still regretted that specific moment.",
    "Doris enjoyed tapping her nails on the table to annoy everyone.",
    "They looked up at the sky and saw a million stars.",
    "The tortoise jumped into the lake with dreams of becoming a sea turtle.",
    "The lake is a long way from here.",
    "He had reached the point where he was paranoid about being paranoid.",
    "Sometimes it is better to just walk away from things and go back to them later when you’re in a better frame of mind.",
    "A glittering gem is not enough.",
    "Don't put peanut butter on the dog's nose.",
    "The thick foliage and intertwined vines made the hike nearly impossible.",
    "Peanuts don't grow on trees, but cashews do.",
    "I think I will buy the red car, or I will lease the blue one.",
    "The urgent care center was flooded with patients after the news of a new deadly virus was made public.",
    "I am happy to take your donation; any amount will be greatly appreciated.",
    "Grape jelly was leaking out the hole in the roof.",
    "Nothing is as cautiously cuddly as a pet porcupine.",
    "He was surprised that his immense laziness was inspirational to others.",
    "Courage and stupidity were all he had.",
    "His thought process was on so many levels that he gave himself a phobia of heights.",
    "He wondered if she would appreciate his toenail collection.",
    "A suit of armor provides excellent sun protection on hot days.",
    "Nobody questions who built the pyramids in Mexico.",
    "Lets all be unique together until we realise we are all the same.",
    "The urgent care center was flooded with patients after the news of a new deadly virus was made public.",
    "Sometimes I stare at a door or a wall and I wonder what is this reality, why am I alive, and what is this all about?",
    "Greetings from the galaxy MACS0647-JD, or what we call home.",
    "He turned in the research paper on Friday; otherwise, he would have not passed the class.",
    "He shaved the peach to prove a point.",
    "I don’t respect anybody who can’t tell the difference between Pepsi and Coke.",
    "If any cop asks you where you were, just say you were visiting Kansas.",
    "As the years pass by, we all know owners look more and more like their dogs.",
    "He looked behind the door and didn't like what he saw.",
    "When he encountered maize for the first time, he thought it incredibly corny.",
    "Car safety systems have come a long way, but he was out to prove they could be outsmarted.",
    "She was disgusted he couldn’t tell the difference between lemonade and limeade.",
    "Malls are great places to shop; I can find everything I need under one roof.",
    "She did not cheat on the test, for it was not the right thing to do.",
    "On a scale from one to ten, what's your favorite flavor of random grammar?",
    "Joe made the sugar cookies; Susan decorated them.",
    "The tortoise jumped into the lake with dreams of becoming a sea turtle.",
    "They were excited to see their first sloth.",
    "He said he was not there yesterday; however, many people saw him there.",
    "Pink horses galloped across the sea.",
    "The stranger officiates the meal.",
    "We need to rent a room for our party.",
    "He realized there had been several deaths on this road, but his concern rose when he saw the exact number.",
    "There were white out conditions in the town; subsequently, the roads were impassable.",
    "Joyce enjoyed eating pancakes with ketchup.",
    "He uses onomatopoeia as a weapon of mental destruction.",
    "The view from the lighthouse excited even the most seasoned traveler.",
    "Various sea birds are elegant, but nothing is as elegant as a gliding pelican.",
    "Everybody should read Chaucer to improve their everyday vocabulary.",
    "She saw the brake lights, but not in time.",
    "The secret code they created made no sense, even to them.",
    "It's much more difficult to play tennis with a bowling ball than it is to bowl with a tennis ball.",
    "A suit of armor provides excellent sun protection on hot days.",
    "She saw no irony asking me to change but wanting me to accept her for who she is.",
    "The clock within this blog and the clock on my laptop are 1 hour different from each other.",
    "Her hair was windswept as she rode in the black convertible.",
    "Peanuts don't grow on trees, but cashews do.",
    "8% of 25 is the same as 25% of 8 and one of them is much easier to do in your head.",
    "It dawned on her that others could make her happier, but only she could make herself happy.",
    "Jeanne wished she has chosen the red button.",
    "Not all people who wander are lost.",
    "It was her first experience training a rainbow unicorn.",
    "I want to buy a onesie… but know it won’t suit me.",
    "I would be delighted if the sea were full of cucumber juice.",
    "Garlic ice-cream was her favorite.",
    "He wondered if it could be called a beach if there was no sand.",
    "Please tell me you don't work in a morgue.",
    "The minute she landed she understood the reason this was a fly-over state.",
    "It had been sixteen days since the zombies first attacked.",
    "The fish dreamed of escaping the fishbowl and into the toilet where he saw his friend go.",
    "At that moment she realized she had a sixth sense.",
    "When motorists sped in and out of traffic, all she could think of was those in need of a transplant.",
    "The father died during childbirth.",
    "She couldn't decide of the glass was half empty or half full so she drank it.",
    "It was her first experience training a rainbow unicorn.",
    "It had been sixteen days since the zombies first attacked.",
    "People who insist on picking their teeth with their elbows are so annoying!",
    "Dan ate the clouds like cotton candy.",
    "Getting up at dawn is for the birds.",
    "He put heat on the wound to see what would grow.",
    "She let the balloon float up into the air with her hopes and dreams.",
    "I was very proud of my nickname throughout high school but today- I couldn’t be any different to what my nickname was.",
    "The river stole the gods.",
    "She found his complete dullness interesting.",
    "Lucifer was surprised at the amount of life at Death Valley.",
    "It was the scarcity that fueled his creativity.",
    "We should play with legos at camp.",
    "Grape jelly was leaking out the hole in the roof.",
    "Fluffy pink unicorns are a popular status symbol among macho men.",
    "Everyone says they love nature until they realize how dangerous she can be.",
    "The book is in front of the table.",
    "The rain pelted the windshield as the darkness engulfed us.",
    "Of course, she loves her pink bunny slippers.",
    "He barked orders at his daughters but they just stared back with amusement.",
    "The secret ingredient to his wonderful life was crime.",
    "Greetings from the real universe.",
    "The elephant didn't want to talk about the person in the room.",
    "It was a really good Monday for being a Saturday.",
    "There are no heroes in a punk rock band.",
    "It turns out you don't need all that stuff you insisted you did.",
    "Before he moved to the inner city, he had always believed that security complexes were psychological.",
    "The green tea and avocado smoothie turned out exactly as would be expected.",
    "He liked to play with words in the bathtub.",
    "It was at that moment that he learned there are certain parts of the body that you should never Nair.",
    "When money was tight, he'd get his lunch money from the local wishing well.",
    "The truth is that you pay for your lifestyle in hours.",
    "Iguanas were falling out of the trees.",
    "His mind was blown that there was nothing in space except space itself.",
    "There's an art to getting your way, and spitting olive pits across the table isn't it.",
    "He was the type of guy who liked Christmas lights on his house in the middle of July.",
    "He dreamed of eating green apples with worms.",
    "Courage and stupidity were all he had.",
    "Each person who knows you has a different perception of who you are.",
    "She cried diamonds.",
    "A suit of armor provides excellent sun protection on hot days.",
    "He found a leprechaun in his walnut shell.",
    "The view from the lighthouse excited even the most seasoned traveler.",
    "They say that dogs are man's best friend, but this cat was setting out to sabotage that theory.",
    "The snow-covered path was no help in finding his way out of the back-country.",
    "Andy loved to sleep on a bed of nails."
)

internal val simpleData = List(messages.size) {
    val message = messages[it]

    SimpleData(
        id = UUID.randomUUID().toString(),
        ownerId = users.random(),
        isPublic = listOf(true, true, false).random(),
        title = message.split(" ").take(3).joinToString(separator = " "),
        description = message
    )
}

