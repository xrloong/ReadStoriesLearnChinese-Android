package idv.gaozao.readstory;

import idv.gaozao.readstory.models.Article;
import idv.gaozao.readstory.models.Catalog;
import idv.gaozao.readstory.models.Catalog.CatalogItem;
import idv.gaozao.readstory.models.ReadStories;
import idv.gaozao.readstory.models.ReadStoriesMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class XmlDataParser {
	private static final String TAG_READ_STORIES = "read-stores";

	private static final String TAG_READING = "reading";
	private static final String TAG_TITLE = "title";
	private static final String TAG_TRANSLATION = "translation";
	private static final String TAG_MEANING = "meaning";
	private static final String TAG_CONTENT = "content";

	private static final String TAG_CATALOG = "catalog";
	private static final String TAG_ITEM = "item";
	private static final String ATTRIBUTE_KEY = "key";
	private static final String ATTRIBUTE_BIDI = "bidi";
	private static final String ATTRIBUTE_TYPE = "type";

	private static final String ATTRIBUTE_VALUE_BIDI_LTR = "ltr";
	private static final String ATTRIBUTE_VALUE_BIDI_RTL = "rtl";
//	private static final String ATTRIBUTE_VALUE_BIDI_ANY = "any";

	private static final String TAG_HELP = "help";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_SENTENCE = "sentence";
	private static final String TAG_BR = "br";


	private static String parseText(XmlPullParser parser, String tagName) {
		String text = "";

		outer: try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				int type = parser.getEventType();
				String name = parser.getName();
				switch (type) {
				case XmlPullParser.START_TAG:
					break;
				case XmlPullParser.END_TAG:
					if (tagName.equals(name))
						break outer;
					break;
				default:
				case XmlPullParser.TEXT:
					text = parser.getText();
					break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return text;
	}

	private static AnnotationString parseAnnotationText(XmlPullParser parser, String tagName) {
		String text = parseText(parser, tagName);
		return new AnnotationString(text);
	}

	private static AnnotationString parseAnnotationParagraph(XmlPullParser parser, String tagName) {
		List<String> textList = new ArrayList<String>();
		outer: try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				int type = parser.getEventType();
				String name = parser.getName();
				switch (type) {
				case XmlPullParser.START_TAG:
					if (TAG_SENTENCE.equals(name)) {
						String text = parseText(parser, TAG_SENTENCE);
						textList.add(text);
					} else if (TAG_BR.equals(name)) {
						parseText(parser, TAG_BR);
						textList.add("\n");
					}

					break;
				case XmlPullParser.END_TAG:
					if (tagName.equals(name))
						break outer;
					break;
				default:
				case XmlPullParser.TEXT:
					break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String message = join(textList);
		return new AnnotationString(message);
	}


	private static Article readXML(XmlPullParser parser) {
		AnnotationString title = new AnnotationString();
		AnnotationString translation = new AnnotationString();
		AnnotationString meaning = new AnnotationString();
		AnnotationString content = new AnnotationString();

		outer: try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				int type = parser.getEventType();
				String name = parser.getName();
				switch (type) {
				case XmlPullParser.START_TAG:
					if (TAG_TITLE.equals(name)) {
						title = parseAnnotationText(parser, TAG_TITLE);
					} else if (TAG_TRANSLATION.equals(name)) {
						translation = parseAnnotationText(parser, TAG_TRANSLATION);
					} else if (TAG_MEANING.equals(name)) {
						meaning = parseAnnotationText(parser, TAG_MEANING);
					} else if (TAG_CONTENT.equals(name)) {
						content = parseAnnotationText(parser, TAG_CONTENT);
					}
					break;
				case XmlPullParser.END_TAG:
					if (TAG_READING.equals(name))
						break outer;
					break;
				default:
				case XmlPullParser.TEXT:
					break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Article article = new Article();
		article.setTitle(title);
		article.setTranslation(translation);
		article.setMeaning(meaning);
		article.setContent(content);

		return article;
	}

	public static ReadStories readReadStories(XmlPullParser parser) {
		ReadStories readStories = new ReadStories();

		outer: try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				int type = parser.getEventType();
				String name = parser.getName();
				switch (type) {
				case XmlPullParser.START_TAG:
					if (TAG_READING.equals(name)) {
						String bidi = parser.getAttributeValue("", ATTRIBUTE_BIDI);
						boolean isRTL = ATTRIBUTE_VALUE_BIDI_RTL.equals(bidi);
						Article article = readXML(parser);
						if(isRTL) {
							article.toRTL();
						}
						readStories.setArticle(article);
					} else if (TAG_CATALOG.equals(name)) {
						Catalog catalog = readCatalog(parser);
						readStories.setCatalog(catalog);
					} else if (TAG_MESSAGE.equals(name)) {
						ReadStoriesMessage message = readReadStoriesMessage(parser);
						readStories.setMessage(message);
					}
					break;
				case XmlPullParser.END_TAG:
					if (TAG_READ_STORIES.equals(name))
						break outer;
					break;
				default:
				case XmlPullParser.TEXT:
					break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return readStories;
	}

	private static CatalogItem readCatalogItem(XmlPullParser parser) {
		String keyValue = "";
		String text = "";
		String bidi = ATTRIBUTE_VALUE_BIDI_LTR;
		outer: try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				int type = parser.getEventType();
				String name = parser.getName();
				switch (type) {
				case XmlPullParser.START_TAG:
					keyValue = parser.getAttributeValue("", ATTRIBUTE_KEY);
					bidi = parser.getAttributeValue("", ATTRIBUTE_BIDI);
					break;
				case XmlPullParser.END_TAG:
					if (TAG_ITEM.equals(name))
						break outer;
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
				default:
					break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean isRTL = ATTRIBUTE_VALUE_BIDI_RTL.equals(bidi);
		AnnotationString title = new AnnotationString(text);
		if(isRTL) {
			title.toRTL();
		}
		Catalog.CatalogItem item = new Catalog.CatalogItem(title, keyValue);
		return item;
	}

	private static Catalog readCatalog(XmlPullParser parser) {
		Catalog catalog = new Catalog();
		outer: try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				int type = parser.getEventType();
				String name = parser.getName();
				switch (type) {
				case XmlPullParser.START_TAG:
					if (TAG_ITEM.equals(name)) {
						Catalog.CatalogItem item = readCatalogItem(parser);
						catalog.add(item);
					}
					break;
				case XmlPullParser.END_TAG:
					if (TAG_CATALOG.equals(name))
						break outer;
					break;
				default:
				case XmlPullParser.TEXT:
					break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return catalog;
	}

	private static ReadStoriesMessage readReadStoriesMessage(XmlPullParser parser) {
		ReadStoriesMessage message = new ReadStoriesMessage();
		outer: try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				int type = parser.getEventType();
				String name = parser.getName();
				switch (type) {
				case XmlPullParser.START_TAG:
					if (TAG_MESSAGE.equals(name)) {
						String typeValue = parser.getAttributeValue("", ATTRIBUTE_TYPE);
						message.setType(typeValue);
					}
					break;
				case XmlPullParser.END_TAG:
					if (TAG_MESSAGE.equals(name))
						break outer;
					break;
				default:
				case XmlPullParser.TEXT:
					break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return message;
	}

	public static AnnotationString readHelpText(Context context) {
		int articleResourceId = R.xml.help;

		XmlPullParser parser = context.getResources().getXml(articleResourceId);
		return parseHelp(parser);
	}

	public static AnnotationString parseHelp(XmlPullParser parser) {
		AnnotationString help = new AnnotationString();
		outer: try {
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				int type = parser.getEventType();
				String name = parser.getName();
				switch (type) {
				case XmlPullParser.START_TAG:
					if (TAG_MESSAGE.equals(name)) {
						help = parseAnnotationParagraph(parser, TAG_MESSAGE);
					}
					break;
				case XmlPullParser.END_TAG:
					if (TAG_HELP.equals(name))
						break outer;
					break;
				default:
				case XmlPullParser.TEXT:
					break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return help;
	}

	private static String join(Collection<String> c) {
	    StringBuilder sb=new StringBuilder();
	    for(String s: c)
	        sb.append(s);
	    return sb.toString();
	}

	public static ReadStories loadReadStories(Reader reader) {
		ReadStories readStories = new ReadStories();

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(reader);
			readStories = XmlDataParser.readReadStories(parser);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return readStories;
	}

	public static ReadStories loadReadStories(File file) {
		ReadStories readStories = new ReadStories();

		try {
			FileReader reader = new FileReader(file);
			readStories =  loadReadStories(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return readStories;
	}

	public static ReadStories loadReadStories(String strReadStories) {
		Reader reader = getStringReader(strReadStories);
		return XmlDataParser.loadReadStories(reader);
	}


	private static Reader getStringReader(String strReadStories) {
		return new StringReader(strReadStories);
	}
}
